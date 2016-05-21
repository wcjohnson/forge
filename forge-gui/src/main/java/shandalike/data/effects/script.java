package shandalike.data.effects;

import java.util.Map;

import shandalike.Model;
import shandalike.data.entity.Entity;
import shandalike.data.world.MapState;
import shandalike.data.world.WorldState;

public class script extends Effect {
	String name;
	Map<String,String> parameters;

	@Override
	public void execute(Entity source, Entity target, MapState map, WorldState world) {
		if(name == null) return;
		System.out.println("[Shandalike] Executing script " + name);
		Model.script.runScriptedEffect(name, source, target, map, world, parameters);
	}

}
