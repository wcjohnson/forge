package shandalike.data.world;

import java.util.HashMap;
import java.util.Map;

import shandalike.Model;
import shandalike.data.World;

public class WorldState {
	// Id of the baseWorld this state represents
	public String worldId;
	transient World world;
	// State of submaps
	public Map<String, MapState> maps;
	// Currently active submap
	public String activeMapId;
	// Game time as of last save.
	public float gameTime = 0.0f;
	
	public WorldState(World world) {
		this.world = world;
		this.worldId = world.id;
		maps = new HashMap<String,MapState>();
	}
	
	public void hydrate() {
		if(world != null) { world.hydrate(); return; }
		world = Model.worlds.get(worldId);
		if(world == null) throw new IllegalArgumentException(String.format("Missing world with id '%s' while loading worldState", worldId));
		world.hydrate();
	}
	
	public World getBaseWorld() {
		hydrate(); return world;
	}
	
	public void initializeMapState(String mapId, String mapInfoId) {
		// Hydrate this state
		hydrate();
		// Already initialized this map state
		if(maps.get(mapId) != null) return;
		// Get mapInfo from world
		MapInfo mapInfo = world.getMapInfo(mapInfoId);
		if(mapInfo == null) throw new IllegalArgumentException(String.format("Missing mapInfo with id '%s' in world with id '%s'", mapInfoId, world.id));
		mapInfo.hydrate();
		// Initialize the mapState for this map
		MapState mapState = new MapState();
		mapState.id = mapId;
		mapInfo.initializeMapState(this, mapState);
		maps.put(mapId,  mapState);
	}
	
	public MapState getWorldMapState() {
		return maps.get(world.worldMapId);
	}
	
	public MapState getActiveMapState() {
		if(activeMapId != null)
			return maps.get(activeMapId);
		else
			return null;
	}
	
	public void transitionToMap(String mapId, String mapInfoId, boolean suppressEvent) {
		String previousMapId = activeMapId;
		if(activeMapId != null && activeMapId.equals(mapId)) return;
		if(activeMapId != null) {
			// Stop player motion on leaving map
			getActiveMapState().clearMovementMarker();
			getActiveMapState().getPlayerPawn().velocity.set(0,0);
			// Execute transitionOut script for previous map
			getActiveMapState().getMapInfo().onLeave(getActiveMapState());
		}
		initializeMapState(mapId, mapInfoId);
		System.out.println("[Shandalike] transitionToMap: transitioning from map id " + previousMapId + " to map id " + mapId);
		// Store previous map
		maps.get(mapId).previousMapId = previousMapId;
		activeMapId = mapId;
		// Suppress collision on everything near us on the new map.
		// This prevents "double zoning".
		getActiveMapState().suppressPlayerCollision();
		// Stop automated or carried-over motion for player
		getActiveMapState().clearMovementMarker();
		getActiveMapState().getPlayerPawn().velocity.set(0,0);
		// Run onEnter script for map
		getActiveMapState().getMapInfo().onEnter(getActiveMapState());
		// Notify of map switch.
		if(!suppressEvent) Model.playerDidChangeMap();
		System.out.println("[Shandalike] transitionToMap: transition done");
	}
	
	public void transitionToMap(String mapId, String mapInfoId) {
		transitionToMap(mapId, mapInfoId, false);
	}
	
	public void exitMap() {
		MapState ms = getActiveMapState();
		if(ms == null || ms.previousMapId == null) return;
		transitionToMap(ms.previousMapId, null);
	}

}
