package shandalike.data.effects;

import shandalike.data.entity.Entity;
import shandalike.data.world.MapState;
import shandalike.data.world.WorldState;

public class enterMap extends Effect {
	public String mapId;
	public String mapInfoId;
	
	@Override
	public void execute(Entity source, Entity target, MapState map, WorldState world) {
		world.transitionToMap(mapId, mapInfoId);
	}
	
	
}
