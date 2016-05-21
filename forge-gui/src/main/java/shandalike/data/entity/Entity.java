package shandalike.data.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import shandalike.IScriptingVars;
import shandalike.Model;
import shandalike.data.JSON;
import shandalike.data.behavior.Behavior;
import shandalike.data.behavior.Behaviors;
import shandalike.data.behavior.IBehavioral;
import shandalike.data.entity.thought.ThinkState;
import shandalike.data.entity.thought.Thought;
import shandalike.data.world.MapState;

public class Entity implements IScriptingVars, IBehavioral {
	/** Unique ID of this entity in the enclosing MapState. */
	public String id;
	/** Scripted variables. */
	public Map<String,Object> var;
	/** List of behaviors affecting this entity */
	protected Behaviors behaviors;
	/** Processing that runs each frame on this entity. */
	transient List<Thought> thoughts;
	
	/**
	 * Invoked when a world is loaded with this Entity in it.
	 * Can load assets related to this entity, start up Spawners, etc.
	 */
	public void load() {
		
	}
	
	/**
	 * Invoked when a world is unloaded with this entity in it.
	 */
	public void unload() {
		
	}

	/** 
	 * Execute per frame processing on this entity. 
	 */
	public void think(ThinkState thinkState) {
		if(thoughts == null) return;
		for(Thought t: thoughts) {
			t.think(this, thinkState);
		}
	}
		
	public Entity deepCopy() {
		// I don't like what I've become.
		String j = JSON.toJson(this, Entity.class);
		return JSON.fromJson(j, Entity.class);
	}

	@Override
	public Object getVar(String key) {
		if(var == null) return null;
		return var.get(key);
	}
	
	@Override
	public void setVar(String key, Object value) {
		if(var == null) var = new HashMap<String, Object>();
		if(value == null) {
			var.remove(key);
			// Don't clutter json with empty maps
			if(var.size() == 0) var = null;
		} else {
			var.put(key, value);
		}
	}
	
	public Collection<Behavior> getBehaviors() {
		// Special case: if this is a PlayerPawn, we want the player's Behaviors.
		if(this instanceof PlayerPawn) {
			return Model.adventure.getPlayer().behaviors;
		}
		if(behaviors != null) return behaviors; else return Behavior.noBehaviors;
	}
	
	public void addBehavior(Behavior behavior) {
		Collection<Behavior> beh = getBehaviors();
		if(beh == Behavior.noBehaviors) {
			behaviors = new Behaviors();
			beh = behaviors;
		}
		beh.add(behavior);
		behavior.handleEvent("behaviorDidAdd", this, null, null);
		return;
	}
	
	public void removeBehavior(Behavior behavior) {
		Collection<Behavior> beh = getBehaviors();
		if(beh == Behavior.noBehaviors) return;
		behavior.handleEvent("behaviorWillRemove", this, null, null);
		beh.remove(behavior);
	}
	
	public void handleEvent(String eventType, Object arg1, Object arg2) {
		for(Behavior beh: getBehaviors()) {
			beh.handleEvent(eventType, this, arg1, arg2);
		}
	}
	
	/**
	 * @return The MapState containing this entity.
	 */
	public MapState getMapState() {
		return Model.adventure.getWorldState().getActiveMapState();
	}
}
