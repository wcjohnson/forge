package shandalike.screens;

import java.awt.Container;

/**
 * A panel embeddable in the Shandalike root panel.
 * @author wcj
 */
public interface IPanel {
	/**
	 * Called when the panel will be pushed to the view stack.
	 * @return Whether the panel should be pushed
	 */
	public boolean panelWillPush();
	
	/**
	 * Mount the panel in the given container
	 * @param container The container to mount the panel in. It will be an empty container. The panel is
	 * responsible for setting up and laying out the container.
	 */
	public void mount(Container container);
	
	/**
	 * Called when the panel is presented to the user. Is always called after mount(), but may also be called if
	 * the panel is still mounted but the user transitioned to another screen.
	 */
	public void panelWasShown();
	
	/**
	 * Called when the panel wants to be popped off the view stack.
	 * @return whether the panel should be remocved
	 */
	public boolean panelWillPop();
	
	/**
	 * Unmount the panel.
	 */
	public void unmount(Container container);
}
