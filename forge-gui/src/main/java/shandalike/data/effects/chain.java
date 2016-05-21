package shandalike.data.effects;

import java.util.ArrayList;

import shandalike.data.entity.Entity;
import shandalike.data.world.MapState;
import shandalike.data.world.WorldState;

/**
 * Chains together multiple Effects. Each effect in the chain is executed in sequence.
 * @author wcj
 */
public class chain extends Effect {
	public ArrayList<Effect> effects;

	@Override
	public void execute(Entity source, Entity target, MapState map, WorldState world) {
		if(effects == null) return;
		for(Effect e: effects) {
			e.execute(source, target, map, world);
		}
	}
}
