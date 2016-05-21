package shandalike.data.effects;

import shandalike.data.entity.Entity;
import shandalike.data.world.MapState;
import shandalike.data.world.WorldState;

public class debugPrint extends Effect {
	
	public String message;

	@Override
	public void execute(Entity source, Entity target, MapState map, WorldState world) {
		System.out.println(String.format("[Shandalike] Entity %s debug message: %s", source.id, message));
	}

}
