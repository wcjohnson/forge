package shandalike.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;

import shandalike.Model;
import shandalike.data.entity.Entity;
import shandalike.data.entity.Pawn;
import shandalike.data.entity.Renderable;
import shandalike.data.entity.thought.ThinkState;
import shandalike.data.world.MapState;
import shandalike.data.world.WorldState;

public class GameMap {
	TiledMap map;
	TiledMapRenderer mapRenderer;
	MapState mapState;
	WorldState worldState;
	boolean blockedTiles[][];
	Controller controller;
	ThinkState thinkState;
	CollisionState collisionState;
	BuffRenderer buffRenderer = new BuffRenderer();
	float tileWidth = 32;
	float dx=1, dy=1;
	public boolean zoomOut = false;
	
	public GameMap(Controller controller) {
		this.controller = controller;
		thinkState = new ThinkState();
		collisionState = new CollisionState();
		this.resize(dx, dy);
	}
	
	void setMapState(MapState mapState) {
		this.mapState = mapState;
		setZoomOut(false);
		map = mapState.getMapInfo().tileMap;
		// Arrange matters so that one game unit is equal to one map tile.
		int tw = (int) map.getProperties().get("tilewidth");
		tileWidth = (float)tw;
		mapRenderer = new OrthogonalTiledMapRenderer(map, 1.0f / tileWidth);
		// Load map data
		loadBlockedTiles();
		// Execute load() on map entities.
		for(Entity e: mapState.entities.values()) {
			e.load();
		}
	}
	
	void loadBlockedTiles() {
		collisionState.clearBlocks();
		// Get collision layer from map.
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("collision");
		if(layer == null) return;
		// ALl tiles in the collision layer constitute 1x1 blockers.
		for(int i=0; i<layer.getHeight(); i++) {
			for(int j=0; j<layer.getWidth(); j++) {
				final TiledMapTileLayer.Cell cell = layer.getCell(j, i);
				if(cell == null) continue;
				TiledMapTile tile = cell.getTile();
				if(tile != null) {
					collisionState.addBlock(new Rectangle((float) j, (float) i, 1.0f, 1.0f));
				}
			}
		}
	}
	
	public void setZoomOut(boolean zoomout) {
		if(!this.mapState.canZoomOut()) zoomout = false;
		this.zoomOut = zoomout;
	}
	
	void render(RenderState renderState) {		
		// Nothing to render
		if(mapState == null || mapState.entities == null) return;
		
		// Do thinking
		if(!renderState.gameTimeController.isPaused()) {
			// Prepare think state
			thinkState.t = renderState.t; thinkState.dt = renderState.dt;
			thinkState.gameTime = renderState.gameTime;
			thinkState.mapState = mapState;
			thinkState.collisionState = collisionState;
			thinkState.worldState = worldState;
			collisionState.pixelsPerUnit = controller.renderState.pixelsPerUnit;
			
			for(Entity e: mapState.entities.values()) {
				e.think(thinkState);
			}
		}

		// Center camera on player
		Pawn p = mapState.getPlayerPawn();
		renderState.playerPawn = p;
		// Minimap vs maximap
		if(zoomOut) {
			renderState.zoomOut = true;
			renderState.placeCamera( ((float)mapState.getMapInfo().width) / 2.0f, ((float)mapState.getMapInfo().height) / 2.0f);
			renderState.orthoFit(mapState.getMapInfo().width, mapState.getMapInfo().height);
		} else {
			renderState.placeCamera(p.pos.x, p.pos.y);
			renderState.orthoToScreen();
			renderState.zoomOut = false;
		}

		// Draw map
		mapRenderer.setView(renderState.camera);
		mapRenderer.render();
		
		// Draw entities
		renderState.batch.setProjectionMatrix(renderState.camera.combined);
		renderState.batch.begin();
		for(Entity e: mapState.entities.values()) {
			if(e instanceof Renderable) {
				((Renderable) e).render(renderState);
			}
		}
		renderState.batch.end();
		
		// Draw UI stuff.
		buffRenderer.render(renderState, Model.adventure.getPlayer().getBehaviors());
	}

	public void resize(float arg0, float arg1) {
		dx = arg0; dy = arg1;
		// Pixel-for-pixel ortho projection
		controller.renderState.setupCamera(dx, dy, tileWidth);
	}

	public void click(float x, float y) {
		if(mapState == null) return;
		// No click motion on dungeon screens
		if(mapState.getMapInfo().snapToTiles) return;
		// Eliminate existing move destination
		mapState.removeEntityById("_move_target");
		// Create a new move destination at the designated coordinates.
		Pawn p = mapState.addEntity("_move_target", Pawn.class);
		p.spriteAsset = "crosshair.sprite.json"; p.load();
		p.pos.x = x; p.pos.y = y;
	}

	public void setWorldState(WorldState worldState2) {
		worldState = worldState2;
	}

}
