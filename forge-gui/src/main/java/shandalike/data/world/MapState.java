package shandalike.data.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.math.Rectangle;

import shandalike.Model;
import shandalike.data.entity.CollidablePawn;
import shandalike.data.entity.Entity;
import shandalike.data.entity.Pawn;
import shandalike.data.entity.town.Town;

public class MapState {
	/** Id of this map within the enclosing WorldState */
	public String id;
	/** Id of the mapInfo object this MapState came from. */
	public String mapInfoId;
	/** Name of this map. */
	public String name;
	/** If the player arrived here from another map, the id of that map. */
	public String previousMapId;
	/** If the player is in a town, the id of that town. */
	public String inTownId;
	transient MapInfo mapInfo;
	// All entities on the map.
	public ConcurrentHashMap<String, Entity> entities;
	// The player pawn entity ID on this map, if player is on map.
	public String playerPawnId;
	
	public MapState() {
		entities = new ConcurrentHashMap<String,Entity>(50, 0.75f, 1);
	}
	
	public Pawn getPlayerPawn() {
		if (playerPawnId == null) return null;
		return (Pawn)entities.get(playerPawnId);
	}
	
	/**
	 * @return The base MapInfo this MapState was created from.
	 */
	public MapInfo getMapInfo() {
		if(mapInfo != null) {
			mapInfo.hydrate();
			return mapInfo;
		}
		mapInfo = Model.adventure.getWorld().getMapInfo(mapInfoId);
		mapInfo.hydrate();
		return mapInfo;
	}
		
	/**
	 * Add a new entity to this MapState, giving it a random GUID. 
	 **/
	public <T extends Entity> T addEntity(Class<T> clazz) {
		T newEnt;
		try {
			newEnt = clazz.newInstance();
			newEnt.id = UUID.randomUUID().toString();
			entities.put(newEnt.id, newEnt);
			return newEnt;
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
	}
	
	/**
	 * Add an entity to this MapState with a specific ID.
	 */
	public <T extends Entity> T addEntity(String id, Class<T> clazz) {
		T newEnt;
		try {
			newEnt = clazz.newInstance();
			newEnt.id = id;
			entities.put(newEnt.id, newEnt);
			return newEnt;
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
	}
	
	/**
	 * Add an entity to this MapState. If it already has an id, it will be added with
	 * that id, replacing any pre-existing entity with the same id. If it does not have an
	 * id, a new guid will be created.
	 */
	public <T extends Entity> T addEntity(T entity) {
		if(entity == null) return null;
		if(entity.id == null) entity.id = UUID.randomUUID().toString();
		entities.put(entity.id, entity);
		return entity;
	}
	
	/**
	 * Remove an entity from this MapState.
	 * @param id
	 */
	public void removeEntityById(String id) {
		entities.remove(id);
	}
	
	public void removeEntity(Entity ent) {
		entities.remove(ent.id);
	}

	/**
	 * Get an entity from this MapState by id.
	 * @param string The entity id.
	 * @return The entity if it exists, null otherwise.
	 */
	public Entity getEntity(String string) {
		return entities.get(string);
	}
	
	public Collection<Entity> getAllEntities() {
		return entities.values();
	}
	
	/**
	 * Get all collidable entities within the given rect.
	 * @param rect
	 * @return
	 */
	public Collection<CollidablePawn> collisionQuery(Rectangle rect) {
		float ppu = this.getMapInfo().getPixelsPerUnit();
		Rectangle tmp = new Rectangle();
		ArrayList<CollidablePawn> hits = new ArrayList<CollidablePawn>();
		for(Entity entity: entities.values()) {
			if(entity instanceof CollidablePawn) {
				((CollidablePawn) entity).getCollisionRectangle(ppu, tmp);
				if(tmp.overlaps(rect)) hits.add((CollidablePawn) entity);
			}
		}
		return hits;
	}
	
	/**
	 * Suppress player collision on nearby objects. Used when player transitions to/from the map to prevent
	 * him from double-zoning.
	 */
	public void suppressPlayerCollision() {
		System.out.println("[Shandalike] suppressPlayerCollision() -- running on mapId " + this.id + " mapInfoId " + this.getMapInfo().id );
		Rectangle r = new Rectangle();
		this.getPlayerPawn().getCollisionRectangle(this.getMapInfo().getPixelsPerUnit(), r);
		for(CollidablePawn p: this.collisionQuery(r)) {
			System.out.println("[Shandalike] suppressPlayerCollision(): suppressing collision with pawn id " + p.id + " of " + p.getClass().toString());
			p.isColliding = true;
		}
	}
	
	/**
	 * Can this map zoom out to minimap?
	 */
	public boolean canZoomOut() {
		return true;
	}

	public void clearMovementMarker() {
		removeEntityById("_move_target");
	}
	
	public void placeMovementMarker(float x, float y, boolean visible) {
		clearMovementMarker();
		Pawn p = addEntity("_move_target", Pawn.class);
		if(visible) { p.spriteAsset = "crosshair.sprite.json"; p.load(); }
		p.pos.x = x; p.pos.y = y;
	}

	/** If player was in town, re-run enter script for town. */
	public void reenterTown() {
		if(this.inTownId == null) return;
		Town t = (Town) this.getEntity(this.inTownId);
		System.out.println("[Shandalike] Restoring user's inTown status for town id" + this.inTownId);
		// Simulate a collideWithPlayerEvent on the town. This will cause the entry script to run.
		t.handleEvent("collideWithPlayer", getPlayerPawn(), null);
	}
}
