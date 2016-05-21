package shandalike.data.effects;

import shandalike.data.entity.Entity;
import shandalike.data.world.MapState;
import shandalike.data.world.WorldState;

/**
 * An Effect is a serializable description of something that can happen in the game world.
 * Effects can Execute themselves, putting their description into practice.
 * @author wcj
 */
public class Effect {
	/**
	 * Execute the effect.
	 * @param source The entity that caused the effect, if any.
	 * @param target The other entity involved in the effect, if any.
	 * @param map The map where the effect was triggered.
	 * @param world The world in which the effect was triggered (always the active world)
	 * @throws Exception 
	 */
	public void execute(Entity source, Entity target, MapState map, WorldState world) {
	}
	
	/**
	 * Execute the effect, if it is given.
	 * @param effect
	 * @param source
	 * @param target
	 * @param map
	 * @param world
	 * @throws Exception 
	 */
	public static void execute(Effect effect, Entity source, Entity target, MapState map, WorldState world) {
		if(effect != null) effect.execute(source, target, map, world);
	}
}
