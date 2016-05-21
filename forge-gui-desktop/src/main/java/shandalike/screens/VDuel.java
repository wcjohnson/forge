package shandalike.screens;

import java.awt.Container;
import forge.UiCommand;
import forge.screens.home.LblHeader;
import forge.toolbox.FScrollPane;
import net.miginfocom.swing.MigLayout;
import shandalike.IGameEventListener;
import shandalike.Model;
import shandalike.mtg.Duel;

/**
 * Pre- and post-duel sub screen for Shandalike.
 * @author wcj
 */
public enum VDuel implements IPanel, IGameEventListener {
	SINGLETON_INSTANCE;
	
	private Duel duel;
		
	/////////////////////////////// Controls
	private final LblHeader lblTitle = new LblHeader("Duel");
    final FScrollPane scrCustom = new FScrollPane(false);
    private final MenuPanel menuPanel = new MenuPanel();
    
    private VDuel() {
    	scrCustom.getViewport().add(menuPanel);
    }
    
    //////////////////////////////// Commands
	@SuppressWarnings("serial")
	private final UiCommand cmdBack = new UiCommand() {
		@Override
		public void run() {
			Model.activeDuel = null;
			Root.popPanel(SINGLETON_INSTANCE);
		}
	};
	
    //////////////////////////////// Lifecycle
	public void update() {
		menuPanel.setModel(duel.menu);
		scrCustom.scrollToTop();
	}
	
	@Override
	public boolean panelWillPush() {
		// Don't allow screen to be pushed twice
		if(Root.peekPanel() == SINGLETON_INSTANCE) return false;
		return true;
	}

	@Override
	public void mount(Container container) {
        container.setLayout(new MigLayout("insets 0, gap 0, wrap, ax right"));
        container.add(lblTitle, "w 80%!, h 40px!, gap 0 0 15px 15px, ax right");
        
        container.add(scrCustom, "w 100%!, h 100%-65px!");
        
        container.revalidate();
        update();
	}

	@Override
	public void panelWasShown() {
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

	/////////////////////////// Game events listener
	@Override
	public void duelWillStart(Duel duel) {
		// Show duel panel on duel will start event
		this.duel = duel;
		Root.pushPanel(SINGLETON_INSTANCE);
		SINGLETON_INSTANCE.update();
	}

	@Override
	public void playerDidChangeMap() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void duelWillCancel(Duel duel) {
		cmdBack.run();		
	}

	@Override
	public void gameEvent(String event, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

}
