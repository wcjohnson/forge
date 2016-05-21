package shandalike.screens;

import java.awt.Canvas;
import java.awt.Container;
import java.awt.EventQueue;
import java.util.Stack;

import javax.swing.JPanel;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;

import forge.Singletons;
import forge.gui.framework.FScreen;
import forge.gui.framework.ICDoc;
import forge.gui.framework.IVTopLevelUI;
import forge.screens.deckeditor.CDeckEditorUI;
import forge.util.gui.SOptionPane;
import forge.view.FView;
import net.miginfocom.swing.MigLayout;
import shandalike.IGameEventListener;
import shandalike.UIModel;
import shandalike.Model;
import shandalike.game.Controller;
import shandalike.mtg.Duel;
import shandalike.mtg.ShopModel;

// OK, so I spent like 10 hours trying to figure out how to make this work well and I finally got
// it to at least sort of work. But it's very quirky. Here's what I found out:
// 1) The LwjglCanvas has to be displayed in the top layer of the LayeredPane that Forge uses to stack
// its views. If it is not in the top layer, Java thinks there's something on top and
// doesn't bother to render the canvas, even if the layers above it are hidden or not opaque.
//
// 2) The canvas can never be completely removed from the UI layout, otherwise Canvas.removeNotify()
// gets called, which shuts down the engine and unloads all the resources. Then they have to be reloaded on
// every transition back to the canvas, which creates noticeable slowdowns. This could probably be worked
// around by making a custom LwjglCanvas-based class.
//
// 3) We have to create and mount the canvas at least as early as the user visiting the Shandalike
// world screen on the main menu. This is because in order to load the .tmx files representing the map,
// we need the gdx AssetLoader, which in turn needs the gdx globals and main loop, which are both controlled
// by the Canvas. Merely creating the canvas isn't enough, it has to be mounted into the swing ui tree and visible.
//
// 4) When mounting the canvas, anything relying on the canvas has to be invokeLater()'d due to the
// threaded nature of the gdx startup/runloop sequence.
//
// 5) Adding and removing things to Forge's top level tab bar is extremely quirky and doesn't quite work the
// way I want it to. For instance, closing a tab on the tab bar always punts the user all the way back to the
// Home screen, rather than taking them back to where they were. I flirted with this approach for awhile but it
// just didn't flow well. Shandalike needs to control what screen the user is seeing.
//
// The upshots of the above quirks are as follows:
//
// 1) There is a single Root component that manages the Shandalike experience. It owns the canvas and maintans
// a stack of screens that let me guide the user around. The Root component plugs into the Forge screen system using
// a new layer that I hacked into the Forge view stack which is always on top of the other layers.
//
// 2) The rest of the app tries to avoid the Forge screen system because of #5. The layer being on top of the Forge
// layer stack screws up some Forge look and feel stuff, like the dark dialog backdrops. Shandalike tries hard
// to prevent the user from switching tabs unless they save/quit Shandalike first. Unless someone who is
// better at forge internals/java gui quirks than me can figure out how to fix this, too bad, I guess.
//
// 3) I had to do some ugly copypasta hacks of some Forge stuff to embed it in the
// Shandalike frame rather than as a separate tab. Again, this would need the attention of someone who is better at
// forge/java quirks than I am. The deck editor had to remain as a separate tab because frankly I couldn't
// understand its MVC code at all.
//
// 4) The Root component mounts and activates the canvas at Forge load time in instantiate(). When the canvas is
// not being utilized, it is shrunk down to 1-pixel big rather than being unmounted from the layout to work around #2.
// We also pause the game and slow down the render loop to avoid thrashing CPU while not rendering anything.
//
// 5) The Root component is laid out as 3 rows -- the top menu/info bar, the subscreen frame, and the canvas. The
// canvas and subscreen frames are expanded/contracted in the layout manager as screen switching is done.

public enum Root implements IVTopLevelUI, ICDoc, IGameEventListener {
	SINGLETON_INSTANCE;
	
	/** The gdx opengl app driver. */
	LwjglCanvas gl;
	/** The top controls menu */
	Controls controls;
	/** The root container */
	Container rootView;
	/** The subscreen container */
	Container subView;
	/** The AWT canvas that gl draws on */
	Canvas canvas;
	/** Is Shandalike open? */
	boolean isActive = false;

	@Override
	public void instantiate() {
    	System.out.println("[Shandalike] Root.instantiate");
    	// Instantiate game canvas.
    	LwjglApplicationConfiguration.disableAudio = true;
    	gl = new LwjglCanvas(new Controller());
    	// Configure the top layer panel
    	rootView = FView.SINGLETON_INSTANCE.getPnlTopLayer();
    	// This panel has to start invisible so we don't overshadow Forge's main UI.
    	rootView.setVisible(false);
    	rootView.setLayout(new MigLayout("insets 0, gap 0, wrap 1"));
    	// Row 1: controls
    	controls = new Controls();
    	rootView.add(controls, "w 100%!, h 50px!");
    	// Row 2: subscreen container
    	JPanel panel = new JPanel();
    	panel.setOpaque(false);
    	subView = panel;
    	rootView.add(subView, "w 100%!, h 100%-51px!");
    	// Row 3: canvas
    	// NOTE: We have to defer this
    	canvas = gl.getCanvas();
    	EventQueue.invokeLater(new Runnable() {
    		@Override public void run() {
    			rootView.add(canvas, "w 100%!, h 1px!");
    			// ... then defer until after GDX initializes, which happens when it's added to the frame...
    			// It's weird that this is necessary, but I couldn't get it to work without it.
    			EventQueue.invokeLater(new Runnable() {
    	    		@Override public void run() {
    	    			canvas.setVisible(false);
    	    			canvas.addFocusListener(Model.gameController.inputManager);
    	    			// Initialized; begin responding to events
    	    			Model.listeners.add(Root.SINGLETON_INSTANCE);
    	    		}
    			});
    		}
    	});
    	
		// Connect views to game events
		Model.listeners.add(VDuel.SINGLETON_INSTANCE);
	}
	
	// Show the canvas, hide the container
	private void showCanvas() {
		System.out.println("[Shandalike] showCanvas()");
		MigLayout layout = (MigLayout)rootView.getLayout();
		// Swap the layout of the canvas and the container; revalidate the layout
		layout.setComponentConstraints(canvas, "w 100%!, h 100%-51px!");
		layout.setComponentConstraints(subView, "w 100%!, h 1px!");
		canvas.setVisible(true);
		subView.removeAll();
		rootView.revalidate();
		// Make the canvas focused for keyboard input
		canvas.requestFocus();
		// Resume the game when landing on the canvas
		Model.gameController.resume();
	}
	
	// Show the container, hide the canvas.
	private void showContainer() {
		System.out.println("[Shandalike] showContainer()");
		MigLayout layout = (MigLayout)rootView.getLayout();
		layout.setComponentConstraints(canvas, "w 100%!, h 1px!");
		layout.setComponentConstraints(subView, "w 100%!, h 100%-51px!");
		canvas.setVisible(false);
		rootView.revalidate();
		// Pause the game when leaving the canvas
		Model.gameController.pause();
	}
	
	
	/**
	 * Invoked when user lands on Shandalike tab.
	 */
	@Override
	public void populate() {
		// Make the top-layer view visible.
    	rootView.setVisible(true);
    	isActive = true;
    	// Restore the view state
    	restoreStack();
	}

	@Override
	public boolean onSwitching(FScreen fromScreen, FScreen toScreen) {
		// Hide the top layer panel so we don't interfere with the rest of Forge's UI.
		showContainer();
		rootView.setVisible(false);
		isActive = false;
		return true;
	}

	@Override
	public boolean onClosing(FScreen screen) {
		// Empty view stack
		viewStack.clear();
		// When leaving the Shandalike tab, make sure to hide the canvas.
		showContainer();
		// Hide the top layer panel so we don't interfere with the rest of Forge's UI.
		rootView.setVisible(false);
		isActive = false;
		// Make Shandalike save and quit the adventure
		Model.closeAdventure();
		return true;
	}
	//////////////////////////// Subscreen stack
	private final Stack<IPanel>  viewStack = new Stack<IPanel>();
	
	private void restoreStack() {
		emerge(false);
	}
	
	// Submerge the top view on the stack.
	private void submerge() {
		if(viewStack.isEmpty()) {
			showContainer();
		} else {
			IPanel top = viewStack.peek();
			top.unmount(subView);
			subView.removeAll();
			subView.revalidate();
			subView.repaint();
		}
	}
	
	// Emerge the top view on the stack.
	private void emerge(boolean needsMount) {
		if(viewStack.isEmpty()) {
			showCanvas();
		} else {
			showContainer();
			IPanel top = viewStack.peek();
			if(needsMount) {
				top.mount(subView);
			}
			top.panelWasShown();
		}
	}
	
	public static void pushPanel(IPanel panel) {
		if(!panel.panelWillPush()) return;
		System.out.println("[Shandalike] Root view: pushing panel of class " + panel.getClass());
		SINGLETON_INSTANCE.submerge();
		SINGLETON_INSTANCE.viewStack.push(panel);
		SINGLETON_INSTANCE.emerge(true);
	}
	
    public static void popPanel(IPanel panel) {
    	Stack<IPanel> stk = SINGLETON_INSTANCE.viewStack;
    	if(stk.isEmpty()) return;
    	// Check if we can unmount this screen
    	IPanel thisScreen = stk.peek();
    	if(!thisScreen.panelWillPop()) return;
    	System.out.println("[Shandalike] Root view: popping panel of class " + panel.getClass());
    	SINGLETON_INSTANCE.submerge();
    	stk.pop();
    	SINGLETON_INSTANCE.emerge(true);
    }
    
    public static IPanel peekPanel() {
    	if(!SINGLETON_INSTANCE.viewStack.empty()) return SINGLETON_INSTANCE.viewStack.peek(); else return null;
    }

	//////////////////////////// ICDoc interface
	@Override
	public void register() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	/////////////////////////////// IGameEventListener
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
		case "reload": {
			// Clear view stack
			while(!viewStack.isEmpty()) popPanel(viewStack.peek());
			break;
		}
		
		case "pushView": {
			UIModel model = (UIModel)arg1;
			MenuScreen view = new MenuScreen();
			view.setModel(model);
			pushPanel(view);
			break;
		}
		case "popView":{
			if(peekPanel() instanceof MenuScreen) popPanel(peekPanel());
			break;
		}
		
		case "openShop": {
			Singletons.getControl().setCurrentScreen(FScreen.QUEST_CARD_SHOP);
	        CDeckEditorUI.SINGLETON_INSTANCE.setEditorController(
	           new CEditorShandalikeCardShop(
	        		   (ShopModel) arg1, 
	        		   CDeckEditorUI.SINGLETON_INSTANCE.getCDetailPicture()
	        	)
	        );
	        break;
		}
		
		case "openDecks": {
			pushPanel(VDecks.SINGLETON_INSTANCE);
			break;
		}
		
		case "showMessageBox": {
			SOptionPane.showMessageDialog((String)arg1, (String)arg2);
			break;
		}
			
		default:
			break;
		}
	}

}
