package shandalike.screens;

import java.awt.Container;

import forge.screens.home.LblHeader;
import forge.toolbox.FScrollPane;
import net.miginfocom.swing.MigLayout;
import shandalike.UIModel;

/**
 * A generic menuscreen driven by a MenuModel.
 * @author wcj
 */
public class MenuScreen implements IPanel {
	/////////////////////////////// Controls
	private final LblHeader lblTitle = new LblHeader("");
    final FScrollPane scrCustom = new FScrollPane(false);
    private final MenuPanel menuPanel = new MenuPanel();
    
    public MenuScreen() {
    	scrCustom.getViewport().add(menuPanel);
    }

	@Override
	public boolean panelWillPush() {
		return true;
	}
	
	public void update() {
		menuPanel.model.update();
	}

	@Override
	public void mount(Container container) {
        container.setLayout(new MigLayout("insets 0, gap 0, wrap, ax right"));
        
        container.add(scrCustom, "w 100%!, h 100%-30px!, gap 0 0 15px 15px");
        
        container.revalidate();
        update();
	}

	@Override
	public void panelWasShown() {
		update();
	}

	@Override
	public boolean panelWillPop() {
		return true;
	}

	@Override
	public void unmount(Container container) {
		
	}
	
	public UIModel getModel() {
		return menuPanel.model;
	}

	public void setModel(UIModel model) {
		menuPanel.setModel(model);
	}

}
