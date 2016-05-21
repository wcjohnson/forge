package shandalike.screens;

import javax.swing.JPanel;

import forge.UiCommand;
import forge.assets.FSkinProp;
import forge.gui.framework.FScreen;
import forge.toolbox.FLabel;
import forge.toolbox.FSkin;
import forge.view.FView;
import net.miginfocom.swing.MigLayout;
import shandalike.Model;
import shandalike.UIModel;
import shandalike.Util;
import shandalike.data.Adventure;

public class Controls extends JPanel {
	private static final long serialVersionUID = 1L;
	
	final FLabel btnSave = new FLabel.ButtonBuilder().text("Save").build();
	final FLabel btnLoad = new FLabel.ButtonBuilder().text("Load").build();
	final FLabel btnQuit = new FLabel.ButtonBuilder().text("Quit").build();
	final FLabel btnCharacter = new FLabel.ButtonBuilder().text("Character").build();
	final FLabel btnJournal = new FLabel.ButtonBuilder().text("Journal").build();
	final FLabel btnMap = new FLabel.ButtonBuilder().text("Map").build();
	final FLabel btnReload = new FLabel.ButtonBuilder().text("!Cheat").build();
	
	final FLabel txtGold = new FLabel.Builder().icon(FSkin.getIcon(FSkinProp.ICO_QUEST_COINSTACK)).text("Gold").build();
	final FLabel txtFood = new FLabel.Builder().icon(FSkin.getIcon(FSkinProp.ICO_QUEST_ELIXIR)).text("Food").build();
	
	@SuppressWarnings("serial")
	final UiCommand doMap = new UiCommand(){
		@Override
		public void run() {
			Model.gameController.toggleMapZoom();
		}
	};
	
	@SuppressWarnings("serial")
	final UiCommand doReload = new UiCommand(){
		@Override
		public void run() {
			System.out.println("[Shandalike] Flushing scripts");
			Model.script.flushScripts();
		}
	};
	
	@SuppressWarnings("serial")
	final UiCommand doQuit = new UiCommand(){
		@Override
		public void run() {
			System.out.println("[Shandalike] Autosave and quit");
			FView.SINGLETON_INSTANCE.getNavigationBar().closeTab(FScreen.SHANDALIKE);
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

		JPanel buttonGrid = new JPanel(new MigLayout("insets 0, flowy, wrap 2"));
		buttonGrid.setOpaque(false);
		buttonGrid.add(btnCharacter, "w 100");
		buttonGrid.add(btnJournal, "w 100");
		buttonGrid.add(btnMap, "w 100");
		btnMap.setCommand(doMap);
		buttonGrid.add(btnSave, "w 100");
		btnSave.setCommand(doSave);
		buttonGrid.add(btnLoad, "w 100");
		btnLoad.setCommand(doLoad);
		buttonGrid.add(btnQuit, "w 100");
		btnQuit.setCommand(doQuit);
		buttonGrid.add(btnReload, "w 100");
		btnReload.setCommand(doReload);
				
		add(buttonGrid);
	}
	
}
