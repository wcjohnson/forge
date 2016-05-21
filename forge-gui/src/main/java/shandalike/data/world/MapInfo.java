package shandalike.data.world;

import java.io.File;
import java.util.Map;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import forge.properties.ForgeConstants;
import shandalike.Model;
import shandalike.data.JSON;
import shandalike.data.effects.Effect;
import shandalike.data.entity.CollidablePawn;
import shandalike.data.entity.Entity;
import shandalike.data.entity.Pawn;
import shandalike.data.entity.PlayerPawn;
import shandalike.data.entity.Positional;

public class MapInfo {
	/** Id of this mapInfo in the world map list */
	public String id;
	/** .tmx asset representing the tilemap */
	public String mapAsset;
	/** Script to execute when player FIRST transitions to this map. */
	public String onFirstEnter;
	/** Effect to execute every time player enters this map. */
	public String onEnter;
	/** Effect to execute every time player leaves this map. */
	public String onLeave;
	
	transient boolean isHydrated = false;
	transient String mapRootPath;
	public transient TiledMap tileMap;
	public transient int width, height;
	public transient int tileWidth;
	public transient boolean snapToTiles;
		
	/**
	 * Load summary information about this map.
	 * @param f File pointing to the maps' root directory
	 */
	public static MapInfo loadSummary(File f) {
		String mapRootPath = f.getPath() + ForgeConstants.PATH_SEPARATOR;
		MapInfo mi = JSON.loadJson(mapRootPath + "map.json", MapInfo.class);
		mi.id = f.getName();
		mi.mapRootPath = mapRootPath;
		return mi;
	}
	
	public void hydrate() {
		if(isHydrated) return;
		// Load basic info about the tile map
		if(Model.gameController == null) {
			width = 1; height = 1;
			return;
		}
		tileMap = Model.assetManager.getAsset(mapAsset, TiledMap.class);
		width = (int) tileMap.getProperties().get("width");
		height = (int) tileMap.getProperties().get("height");
		tileWidth = (int) tileMap.getProperties().get("tilewidth");
		snapToTiles = false;
		String sttProp = (String)tileMap.getProperties().get("snapToTiles");
		if( sttProp != null && sttProp.equals("true") ) { snapToTiles = true; }
		onFirstEnter = (String)tileMap.getProperties().get("onFirstEnter");
		onEnter = (String)tileMap.getProperties().get("onEnter");
		onLeave = (String)tileMap.getProperties().get("onLeave");
		isHydrated = true;
		
		// Invisify the meta layers
		// Do this after isHydrated to prevent infinite loop.
		setMetaLayerVisibility(false);
	}
	
	public void setMetaLayerVisibility(boolean vis) {
		getCollisionLayer().setVisible(vis);
		getMetaObjectLayer().setVisible(vis);
	}
	
	/**
	 * Initialize a NEW map state (user has never been to this map before)
	 * @param worldState The enclosing worldState
	 * @return A new map state
	 */
	public MapState initializeMapState(WorldState worldState, MapState mapState) {
		hydrate();
		mapState.mapInfoId = id;
		mapState.mapInfo = this;
		System.out.println("[Shandalike] Initializing map id " + mapState.id + " from mapInfo id " + id);
		
		// Add the player pawn entity
		Pawn playerPawn = mapState.addEntity("_player", PlayerPawn.class);
		mapState.playerPawnId = "_player";
		
		// Copy all initial entities into the map.
		try {
			Entity[] entities = JSON.loadJson(mapRootPath + "entities.json", Entity[].class);
			if(entities != null)
				for(Entity e: entities) mapState.addEntity(e);
		} catch(Exception e) {}
		
		// Execute map init effects
		for(MapObject o: getMetaObjectLayer().getObjects()) {
			initObject(mapState, o);
		}
		
		// Execute initial spawn effect
		Model.script.pcall(onFirstEnter, "mapFirstEnter", mapState);
		
		return mapState;
	}
	
	public Vector2 getObjectLocation(MapObject o) {
		MapProperties props = o.getProperties();
		float x = ((float)props.get("x"))/((float)tileWidth);
		float y = ((float)props.get("y"))/((float)tileWidth);
		float width = ((float)props.get("width"))/((float)tileWidth);
		float height = ((float)props.get("height"))/((float)tileWidth);
		float cx = x + width/2, cy = y + height/2;
		return new Vector2(cx, cy);
	}

	private void initObject(MapState mapState, MapObject o) {
		// x,y,width,height
		MapProperties props = o.getProperties();
		String name = o.getName();
		if(name == null) name = "(unnamed)";
		String type = (String)props.get("action");
		if(type == null) return;
		float x = ((float)props.get("x"))/((float)tileWidth);
		float y = ((float)props.get("y"))/((float)tileWidth);
		float width = ((float)props.get("width"))/((float)tileWidth);
		float height = ((float)props.get("height"))/((float)tileWidth);
		float cx = x + width/2, cy = y + height/2;
		
		
		try {
			// Player start
			if(type.equals("start")) {
				mapState.getPlayerPawn().pos.set(cx, cy);
				return;
			}
			
			// Trigger region
			if(type.equals("trigger")) {
				CollidablePawn exit = mapState.addEntity(CollidablePawn.class);
				exit.pos.set(cx, cy);
				exit.collisionRectangle = new Rectangle(-(width/2.0f), -(height/2.0f), width, height);
				exit.var = JSON.fromJsonObject((String)props.get("var"));
				exit.collisionScript = (String)props.get("script");
				return;
			}
			
			// Initialization effect
			if(type.equals("init")) {
				String theThing;
				// Spawn an initial entity
				if( (theThing = (String)props.get("entity")) != null ) {
					Entity e = JSON.fromJson(theThing, Entity.class);
					mapState.addEntity(e);
					if(e instanceof Positional) {
						((Positional)e).pos.set(cx, cy);
					}
					return;
				}
				
				// Run an initial script
				if( (theThing = (String)props.get("script")) != null) {
					Object var = null;
					String varJson = (String)props.get("var");
					if(varJson != null) var = JSON.fromJsonObject(varJson);
					Model.script.pcall(theThing, "mapInit", new Object[]{cx,cy,width,height,mapState,var});
					return;
				}

				return;
			}
		} catch(Exception ex) {
			throw new RuntimeException("Error initializing map object named '" + name + "' from mapInfo " + id, ex);
		}
	}
	
	public TiledMapTileLayer getCollisionLayer() {
		hydrate();
		return (TiledMapTileLayer) tileMap.getLayers().get("collision");
	}
	
	public MapLayer getMetaObjectLayer() {
		return tileMap.getLayers().get("meta_obj");
	}

	public float getPixelsPerUnit() {
		hydrate();
		return (float)tileWidth;
	}
	
	public String getTerrainAt(float x, float y) {
		hydrate();
		int tx = (int) Math.floor(x), ty = (int) Math.floor(y);
		String result = "none";
		TiledMapTileLayer.Cell cell;
		TiledMapTile tile;
		for(MapLayer layer: tileMap.getLayers()) {
			if(layer instanceof TiledMapTileLayer) {
				TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;
				if( (cell = tileLayer.getCell(tx, ty)) != null) {
					if( (tile = cell.getTile()) != null) {
						if(tile.getProperties() != null) {
							String terrain = (String) tile.getProperties().get("terrainColor");
							if(terrain != null) result = terrain;
						}
					}
				}
			}
		}
		return result;
	}


	public void onLeave(MapState activeMapState) {
		Model.script.pcall(onLeave, "mapLeave", activeMapState);		
	}
	
	public void onEnter(MapState activeMapState) {
		Model.script.pcall(onEnter, "mapEnter", activeMapState);		
	}
}
