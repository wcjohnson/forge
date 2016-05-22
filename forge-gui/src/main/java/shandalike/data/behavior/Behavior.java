package shandalike.data.behavior;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import shandalike.IScriptingVars;
import shandalike.Model;

/**
 * Attachment between an Entity and the scripting system allowing custom code and event handling to run.
 * @author wcj
 */
public class Behavior implements IScriptingVars {
	public static Collection<Behavior> noBehaviors = Collections.unmodifiableCollection(new ArrayList<Behavior>());
	
	/** Properties of this modifier. */
	private Map<String,Object> var;
	/** What is the script for this modifier? */
	public String script;
	/** Non-unique identifying string for this modifier. */
	public String tag = null;
	
	/**
	 * @return The text to be shown to the player as the title of the modifier.
	 */
	public String getTitle() {
		return (String)Model.script.pcall(script, "getTitle", this);
	}
	
	public String getDescription() {
		return (String)Model.script.pcall(script, "getDescription", this);
	}
	
	public void handleEvent(String eventName, IBehavioral behavioral, Object arg1, Object arg2) {
		Model.script.runScriptedBehavior(script, eventName, this, behavioral, arg1, arg2);
	}
	
	public Object runScript(String eventName, IBehavioral behavioral, Object arg1, Object arg2) {
		return Model.script.pcall(script, eventName, new Object[]{this, behavioral, arg1, arg2});
	}
	
	public boolean isHidden() {
		try {
			return (boolean)Model.script.pcall(script, "isHidden", this);
		} catch(Exception e) {
			return true;
		}
	}
	
	public boolean isHelpful() {
		try {
			return (boolean)Model.script.pcall(script, "isHelpful", this);
		} catch(Exception e) {
			return true;
		}
	}

	@Override
	public void setVar(String key, Object value) {
		if(var == null && value != null) var = new HashMap<String,Object>();
		if(value != null) {
			var.put(key, value);
		} else {
			var.remove(key);
		}	
	}

	@Override
	public Object getVar(String key) {
		if(var == null) return null;
		return var.get(key);
	}
	
	/**
	 * Purge all behaviors with the given tag.
	 * @param actor
	 * @param tag
	 */
	public static void purgeByTag(IBehavioral actor, String tag) {
		for(Behavior beh: actor.getBehaviors()) {
			if(tag.equals(beh.tag)) {
				actor.removeBehavior(beh);
			}
		}
	}
	
}
