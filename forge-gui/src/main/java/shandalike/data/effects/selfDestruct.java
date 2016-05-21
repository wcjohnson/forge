package shandalike.data.effects;

import shandalike.data.entity.Entity;
import shandalike.data.world.MapState;
import shandalike.data.world.WorldState;

public class selfDestruct extends Effect {

	@Override
	public void execute(Entity source, Entity target, MapState map, WorldState world) {
		map.removeEntityById(source.id);
	}

}
