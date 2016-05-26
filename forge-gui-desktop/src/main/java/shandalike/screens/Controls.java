package shandalike.screens;

import javax.swing.JPanel;

import forge.UiCommand;
import forge.assets.FSkinProp;
import forge.gui.framework.FScreen;
import forge.toolbox.FLabel;
import forge.toolbox.FSkin;
import forge.view.FView;
import net.miginfocom.swing.MigLayout;
import shandalike.IGameEventListener;
import shandalike.Model;
import shandalike.UIModel;
import shandalike.Util;
import shandalike.data.Adventure;
import shandalike.mtg.Duel;

public class Controls extends JPanel implements IGameEventListener {
	private static final long serialVersionUID = 1L;
	
	final JPanel buttonGrid = new JPanel(new MigLayout("insets 0, flowy, wrap 2"));
	final FLabel btnSave = new FLabel.ButtonBuilder().text("Save").build();
	final FLabel btnLoad = new FLabel.ButtonBuilder().text("Load").build();
	final FLabel btnQuit = new FLabel.ButtonBuilder().text("Quit").build();
	final FLabel btnCharacter = new FLabel.ButtonBuilder().text("Character").build();
	final FLabel btnJournal = new FLabel.ButtonBuilder().text("Journal").build();
	final FLabel btnReload = new FLabel.ButtonBuilder().text("!Cheat").build();
	
	final FLabel txtGold = new FLabel.Builder().icon(FSkin.getIcon(FSkinProp.ICO_QUEST_COINSTACK)).text("Gold").build();
	final FLabel txtFood = new FLabel.Builder().icon(FSkin.getIcon(FSkinProp.ICO_QUEST_ELIXIR)).text("Food").build();
	final FLabel txtCards = new FLabel.Builder().icon(FSkin.getIcon(FSkinProp.ICO_DECKLIST)).text("Cards").build();
	final FLabel txtDecks = new FLabel.Builder().icon(FSkin.getIcon(FSkinProp.ICO_DECKLIST)).text("Deck").build();
		
	@SuppressWarnings("serial")
	final UiCommand doReload = new UiCommand(){
		@Override
		public void run() {
			Model.script.pcall("cheatScreen", "openScreen", null);
		}
	};
	
	@SuppressWarnings("serial")
	final UiCommand doJournal = new UiCommand(){
		@Override
		public void run() {
			Model.script.pcall("journalScreen", "openScreen", null);
		}
	};
	
	@SuppressWarnings("serial")
	final UiCommand doQuit = new UiCommand(){
		@Override
		public void run() {
			System.out.println("[Shandalike] Autosave and quit");
			Model.adventure.save(0);
			Util.closeShandalike();
		}
	};
		
	
	@SuppressWarnings("serial")
	public void openLoadScreen() {
		UIModel m = new UIModel();
		m.addPanel("Load", "Select slot to load.", null);
		for(int i=0; i<shandalike.Constants.NUM_SAVE_SLOTS; i++) {
			Adventure.SaveSlotInfo ssi = Model.adventure.getSaveSlotInfo(i);
			if(ssi == null) {
				m.addButton("Slot #" + i + " (empty!)", null);
			} else {
				final int fi = i;
				m.addButton(ssi.name + " (" + ssi.timestamp + ")", new UiCommand(){
					@Override
					public void run() {
						Util.popUI();
						Model.adventure.load(fi);
					}
				});
			}
		}
		m.addButton("Cancel", new UiCommand(){
			@Override
			public void run() {
				Util.popUI();
			}
		});
		Util.pushUI(m);
	}
	
	@SuppressWarnings("serial")
	public void openSaveScreen() {
		UIModel m = new UIModel();
		m.addPanel("Save", "Select slot to save.", null);
		for(int i=1; i<shandalike.Constants.NUM_SAVE_SLOTS; i++) {
			final int fi = i;
			Adventure.SaveSlotInfo ssi = Model.adventure.getSaveSlotInfo(i);
			UiCommand cmd = new UiCommand(){
				@Override
				public void run() {
					Util.popUI();
					Model.adventure.save(fi);
				}
			};
			if(ssi == null) {
				m.addButton("Slot #" + i + " (empty!)", cmd);
			} else {
				m.addButton(ssi.name + " (" + ssi.timestamp + ")", cmd);
			}
		}
		m.addButton("Cancel", new UiCommand(){
			@Override
			public void run() {
				Util.popUI();
			}
		});
		Util.pushUI(m);
	}
	
	@SuppressWarnings("serial")
	final UiCommand doLoad = new UiCommand() {
		@Override
		public void run() {
			openLoadScreen();
		}
	};
	
	@SuppressWarnings("serial")
	final UiCommand doSave = new UiCommand() {
		@Override
		public void run() {
			openSaveScreen();
		}
	};
	
	public Controls() {
		super(new MigLayout("insets 5px"));
		setOpaque(false);

		buttonGrid.setOpaque(false);
		buttonGrid.add(btnCharacter, "w 100");
		buttonGrid.add(btnJournal, "w 100");
		btnJournal.setCommand(doJournal);
		buttonGrid.add(btnSave, "w 100");
		btnSave.setCommand(doSave);
		buttonGrid.add(btnLoad, "w 100");
		btnLoad.setCommand(doLoad);
		buttonGrid.add(btnQuit, "w 100");
		btnQuit.setCommand(doQuit);
		buttonGrid.add(btnReload, "w 100");
		btnReload.setCommand(doReload);
		
		JPanel statusGrid = new JPanel(new MigLayout("insets 0, flowy, wrap 2"));
		statusGrid.setOpaque(false);
		statusGrid.add(txtCards, "w 150");
		statusGrid.add(txtDecks, "w 150");
		statusGrid.add(txtGold, "w 150");
		statusGrid.add(txtFood, "w 150");
		
				
		add(buttonGrid);
		add(statusGrid);
		Model.listeners.add(this);
	}
	
	public void update() {
		if(Model.adventure != null) {
			if(!Model.adventure.summary.isCheatEnabled && btnReload.getParent() != null) {
				buttonGrid.remove(btnReload);
				buttonGrid.revalidate();
			}
			if(Model.adventure.summary.isIronMan && btnSave.getParent() != null) {
				buttonGrid.remove(btnSave);
				buttonGrid.remove(btnLoad);
				buttonGrid.revalidate();
			}
			if(Model.adventure.getPlayer() != null) {
				String deckName = "(none)";
				if(Util.getPlayerInventory().activeDeckName != null) deckName = Util.getPlayerInventory().activeDeckName;
				txtCards.setText("Cards: " + Util.getPlayerInventory().cardPool.countAll());
				txtDecks.setText("Deck: " + deckName + " (" + Util.getPlayerInventory().deckAverageValue + ")");
				txtGold.setText("Gold: " + Model.adventure.getPlayer().getInventory().getCurrency("gold"));
				txtFood.setText("Food: " + Model.adventure.getPlayer().getInventory().getCurrency("food"));
			}
		}
	}

	@Override
	public void duelWillStart(Duel duel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerDidChangeMap() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void duelWillCancel(Duel duel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameEvent(String event, Object arg1, Object arg2) {
		switch(event) {
		case "playerInventoryChanged":
		case "reload":
		{
			System.out.println("[Shandalike] Controls: playerInventoryChanged");
			update();
			break;
		}
		
		case "openLoadScreen":
		{
			openLoadScreen();
			break;
		}
		}	
	}
}
