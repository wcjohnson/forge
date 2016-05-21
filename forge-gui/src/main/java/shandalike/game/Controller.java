package shandalike.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import shandalike.IGameEventListener;
import shandalike.Model;
import shandalike.data.entity.town.Town;
import shandalike.data.world.MapState;
import shandalike.mtg.Duel;

public class Controller implements ApplicationListener, IGameEventListener {
	/** Window size */
	int dx = 1, dy = 1;
	
	/** State of the game clock */
	public GameTime gameTime;
	/** Asset retrieval system */
	public AssetManager assetManager;
	/** Render and camera related information */
	public RenderState renderState;
	/** Mouse and key bindings */
	public InputManager inputManager;
	/** Game map */
	public GameMap gameMap;
	public boolean pauseRendering;
	
	/**
	 * Construct a new Controller.
	 * 
	 * NOTE: GDX uses the Android activity model, which means the same Controller may be reused
	 * for future launches of the game. Thus most things should be deferred to the create() lifecycle method.
	 */
	public Controller() {
		gameTime = new GameTime();
		inputManager = new InputManager(this);
	}

	@Override
	public void create() {
		System.out.println("[Shandalike] Controller.create");
		assetManager = Model.assetManager;
		Model.gameController = this;
		Model.listeners.add(this);
		renderState = new RenderState(this);
		gameMap = new GameMap(this);
		gameTime.resume();
		// Attach input to controller
		Gdx.input.setInputProcessor(inputManager);

	}
	
	public void loadActiveAdventure() {
		Model.playerDidChangeMap();
	}

	@Override
	public void dispose() {
		System.out.println("[Shandalike] Controller.dispose");
		Model.gameController = null;
		assetManager = null;
	}

	@Override
	public void pause() {
		System.out.println("[Shandalike] Controller.pause");
		gameTime.pause();
		pauseRendering = true;
		// TODO Auto-generated method stub

	}
	
	/** Pause the game without pausing rendering */
	public void pauseGame() {
		System.out.println("[Shandalike] Controller.pauseGame");
		gameTime.pause();
	}

	@Override
	public void resize(int arg0, int arg1) {
		System.out.println(String.format("[Shandalike] Controller.resize %d x %d", arg0, arg1));
		dx = arg0; dy = arg1;
		gameMap.resize((float)arg0, (float)arg1);
	}

	@Override
	public void resume() {
		System.out.println("[Shandalike] Controller.resume");
		gameTime.resume();
		pauseRendering = false;
		// TODO Auto-generated method stub

	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		renderState.t = gameTime.wallTime();
		renderState.gameTime = gameTime.getGameTime();
		renderState.dt = Gdx.graphics.getDeltaTime();
		renderState.gameTimeController = gameTime;
		renderState.debugRendering = true; // XXX
		if(pauseRendering) { return; }
		
		gameMap.render(renderState);
	}
	
	public void toggleMapZoom() {
		if(gameMap.zoomOut) {
			gameTime.resume();
			gameMap.setZoomOut(false);
		} else {
			gameTime.pause();
			gameMap.setZoomOut(true);
		}
	}

	@Override
	public void duelWillStart(Duel duel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerDidChangeMap() {
		if(Model.adventure != null) {
			// Set initial world state if exists
			gameMap.setWorldState(Model.adventure.getWorldState());
			MapState ms = Model.adventure.getWorldState().getActiveMapState();
			gameMap.setMapState(ms);
			// Clear keymap to prevent extraneous movement
			inputManager.clearKeyMap();
			// Restore town status
			ms.reenterTown();			
		}
	}

	@Override
	public void duelWillCancel(Duel duel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameEvent(String event, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}
}
