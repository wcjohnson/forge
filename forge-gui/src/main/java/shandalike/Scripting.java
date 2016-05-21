package shandalike;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import forge.properties.ForgeConstants;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import shandalike.data.behavior.Behavior;
import shandalike.data.behavior.IBehavioral;
import shandalike.data.entity.Entity;
import shandalike.data.world.MapState;
import shandalike.data.world.WorldState;

public class Scripting {
	GroovyClassLoader loader;
	Map<String,GroovyObject> objForFile = new HashMap<String,GroovyObject>();
	Object[] tmp = new Object[5];
	Object[] modifierTmp = new Object[4];

	public Scripting() {
		flushScripts();
	}
	
	public void flushScripts() {
		System.out.println("[Shandalike] Flushing scripts");
		objForFile.clear();
		loader = new GroovyClassLoader(this.getClass().getClassLoader());
		GroovyObject initer = getScript("init");
		pcall(initer, "run", null);
		loader.addClasspath(Constants.GLOBAL_SHANDALIKE_DIR + "script" + ForgeConstants.PATH_SEPARATOR);
		if(Model.adventure != null) {
			loader.addClasspath(Model.adventure.getWorld().getScriptsDir() + ForgeConstants.PATH_SEPARATOR);
		}
	}
	
	GroovyObject loadFile(File f) {
		Class<?> clazz = null;
		try {
			clazz = loader.parseClass(f);
		} catch (Exception e) {
			throw new RuntimeException("Could not load script", e);
		}
		if(clazz == null) return null;
		try {
			return (GroovyObject) clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Couldn't load script", e);
		}
	}
	
	/**
	 * Get the Groovy object instance for the given script.
	 * @param name
	 * @return
	 */
	public GroovyObject getScript(String name) {
		if(name == null) return null;
		GroovyObject obj = objForFile.get(name);
		if(obj != null) return obj;
		File f = new File(Constants.GLOBAL_SHANDALIKE_DIR + "script" + ForgeConstants.PATH_SEPARATOR + name + ".groovy");
		if(f.isFile()) obj = loadFile(f);
		if(obj == null) {
			if(Model.adventure.getWorld() != null) {
				f = new File(Model.adventure.getWorld().getScriptsDir() + name + ".groovy" );
			}
			if(f.isFile()) obj = loadFile(f);
		}
		if(obj == null) return null;
		objForFile.put(name, obj);
		return obj;
	}
	
	public void runScriptedEffect(String name, Entity thisEntity, Entity otherEntity, MapState mapState, WorldState worldState, Map<String,String> parameters) {
		GroovyObject obj = getScript(name);
		if(obj == null) return;
		Object[] args = { thisEntity, otherEntity, mapState, worldState, parameters };
		pcall(obj, "effect", args);
	}
	
	public void runScriptedBehavior(String name, String event, Behavior modifier, IBehavioral behavioral, Object arg1, Object arg2) {
		GroovyObject obj = getScript(name);
		if(obj == null) return;
		Object[] args = { modifier, behavioral, arg1, arg2 };
		pcall(obj, event, args);
	}
	
	public Object pcall(GroovyObject target, String method, Object arg) {
		if(target == null) return null;
		try {
			return target.invokeMethod(method, arg);
		} catch(Exception e) {
			System.out.println("[Shandalike] pcall: script error:");
			e.printStackTrace();
			return null;
		}
	}
	
	public Object pcall(GroovyObject target, String method, Object[] args) {
		if(target == null) return null;
		try {
			return target.invokeMethod(method, args);
		} catch(Exception e) {
			System.out.println("[Shandalike] pcall: script error:");
			e.printStackTrace();
			return null;
		}
	}
	
	public Object pcall(String target, String method, Object[] args) {
		return pcall(getScript(target), method, args);
	}
	
	public Object pcall(String target, String method, Object arg) {
		return pcall(getScript(target), method, arg);
	}
}
