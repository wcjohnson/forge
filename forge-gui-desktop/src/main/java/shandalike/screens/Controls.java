package shandalike.screens;

import javax.swing.JPanel;

import forge.UiCommand;
import forge.gui.framework.FScreen;
import forge.toolbox.FLabel;
import forge.view.FView;
import net.miginfocom.swing.MigLayout;
import shandalike.Model;

public class Controls extends JPanel {
	private static final long serialVersionUID = 1L;
	
	final FLabel btnSave = new FLabel.ButtonBuilder().text("Save").build();
	final FLabel btnLoad = new FLabel.ButtonBuilder().text("Load").build();
	final FLabel btnQuit = new FLabel.ButtonBuilder().text("Quit").build();
	final FLabel btnCharacter = new FLabel.ButtonBuilder().text("Character").build();
	final FLabel btnJournal = new FLabel.ButtonBuilder().text("Journal").build();
	final FLabel btnMap = new FLabel.ButtonBuilder().text("Map").build();
	final FLabel btnReload = new FLabel.ButtonBuilder().text("!Flush").build();
	
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
		
	public void setCommands() {
		
		
	}
	
	public void openLoad() {
		
	}
	
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
		buttonGrid.add(btnLoad, "w 100");
		buttonGrid.add(btnQuit, "w 100");
		btnQuit.setCommand(doQuit);
		buttonGrid.add(btnReload, "w 100");
		btnReload.setCommand(doReload);
		
		setCommands();
		
		add(buttonGrid);
	}
	
}
