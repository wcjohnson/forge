package shandalike.screens;

import java.awt.Container;
import java.awt.Font;

import javax.swing.SwingConstants;

import forge.Singletons;
import forge.UiCommand;
import forge.deck.DeckProxy;
import forge.game.GameType;
import forge.gui.framework.FScreen;
import forge.itemmanager.DeckManager;
import forge.itemmanager.ItemManagerConfig;
import forge.itemmanager.ItemManagerContainer;
import forge.screens.deckeditor.CDeckEditorUI;
import forge.screens.home.LblHeader;
import forge.toolbox.FLabel;
import net.miginfocom.swing.MigLayout;
import shandalike.Model;
import shandalike.data.character.Inventory;

public enum VDecks implements IPanel {
	SINGLETON_INSTANCE;
	
    private final LblHeader lblTitle = new LblHeader("Decks");

    private final DeckManager lstDecks = new DeckManager(GameType.Shandalike, CDeckEditorUI.SINGLETON_INSTANCE.getCDetailPicture());
    private final FLabel lblInfo = new FLabel.Builder()
        .fontAlign(SwingConstants.LEFT).fontSize(16).fontStyle(Font.BOLD)
        .text("Build or select a deck").build();
    private final FLabel btnNewDeck = new FLabel.ButtonBuilder().text("Build a New Deck").fontSize(16).build();
    private final FLabel btnBack = new FLabel.ButtonBuilder().text("Go Back").fontSize(16).build();
    
    // Shandalike inventory to pull decks from.
    private Inventory inventory;
    
    //////////////////////////////// Commands
    // On deck selected, update player.inventory.activeDeck
	@SuppressWarnings("serial")
	private final UiCommand cmdDeckSelect = new UiCommand() {
		@Override
		public void run() {
			DeckProxy deckProxy = lstDecks.getSelectedItem();
			if(deckProxy != null) {
				inventory.setActiveDeckName(deckProxy.getDeck().getName());
			} else {
				inventory.setActiveDeckName(null);
			}
		}
	};
	// On deck delete, update list
	@SuppressWarnings("serial")
	private final UiCommand cmdDeckDelete = new UiCommand() {
		@Override
		public void run() {
			update();
		}
	};
	// On new deck, jump to deck editor
	@SuppressWarnings("serial")
	private final UiCommand cmdDeckCreate = new UiCommand() {
		@Override
		public void run() {
            Singletons.getControl().setCurrentScreen(FScreen.DECK_EDITOR_QUEST);
            CDeckEditorUI.SINGLETON_INSTANCE.setEditorController(
            	new CEditorShandalike(inventory, CDeckEditorUI.SINGLETON_INSTANCE.getCDetailPicture())
            );
		}
	};
	// On back, pop display off stack
	@SuppressWarnings("serial")
	private final UiCommand cmdBack = new UiCommand() {
		@Override
		public void run() {
			Root.popPanel(SINGLETON_INSTANCE);
		}
	};
	
	public void update() {
		inventory = Model.adventure.getPlayer().getInventory();
    	lstDecks.setSelectCommand(null);
    	lstDecks.setDeleteCommand(null);
    	// Add all decks
    	lstDecks.setPool(inventory.getDeckProxies());
    	lstDecks.setup(ItemManagerConfig.QUEST_DECKS);
    	// Highlight selected deck
    	DeckProxy deck = lstDecks.stringToItem(inventory.activeDeckName);
    	if(deck != null) {
    		lstDecks.setSelectedItem(deck);
    	} else {
    		lstDecks.setSelectedIndex(0);
    		cmdDeckSelect.run();
    	}
        lstDecks.setSelectCommand(cmdDeckSelect);
        lstDecks.setDeleteCommand(cmdDeckDelete);
	}

	@Override
	public boolean panelWillPush() {
		return true;
	}


	@Override
	public void mount(Container container) {
		// TODO Auto-generated method stub
		inventory = Model.adventure.getPlayer().getInventory();

        container.setLayout(new MigLayout("insets 0, gap 0, wrap, ax right"));
        container.add(lblTitle, "w 80%!, h 40px!, gap 0 0 15px 15px, ax right");

        container.add(lblInfo, "w 80%!, h 30px!, gap 0 10% 20px 5px");

        btnNewDeck.setCommand(cmdDeckCreate);
        container.add(btnNewDeck, "w 250px!, h 30px!, ax center, gap 0 10% 0 20px");
        btnBack.setCommand(cmdBack);
        container.add(btnBack, "w 250px!, h 30px!, ax center, gap 0 10% 0 20px");
        container.add(new ItemManagerContainer(lstDecks), "w 80%!, gap 0 10% 0 0, pushy, growy, gapbottom 20px");

        container.revalidate();
        
        update();
	}


	@Override
	public boolean panelWillPop() {
		// TODO Auto-generated method stub
		return true;
	}


	@Override
	public void unmount(Container container) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void panelWasShown() {
		// TODO Auto-generated method stub
		update();
		cmdDeckSelect.run();
	}
	
}
