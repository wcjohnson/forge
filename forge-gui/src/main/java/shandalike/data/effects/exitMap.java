package shandalike.data.effects;

import shandalike.data.entity.Entity;
import shandalike.data.world.MapState;
import shandalike.data.world.WorldState;

public class exitMap extends Effect {

	@Override
	public void execute(Entity source, Entity target, MapState map, WorldState world) {
		System.out.println("[Shandalike] exitMap");
		world.transitionToMap(map.previousMapId, null);
	}

}
