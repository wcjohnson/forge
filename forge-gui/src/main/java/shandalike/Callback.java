package shandalike;

import forge.UiCommand;
import groovy.lang.GroovyObject;

/**
 * A generic callback interface used by Shandalike. Can connect back to Forge UiCommands,
 * Groovy scripts, etc. Implements UiCommand so can be attached to Forge UI elements.
 * @author wcj
 */
@SuppressWarnings("serial")
public class Callback implements UiCommand {
	/**
	 * Callback into an already-instantiated Groovy object
	 * @author wcj
	 */
	public static class ScriptObject extends Callback {
		public GroovyObject target = null;
		public String method;
		public Object[] args;
		
		public ScriptObject(GroovyObject target, String method, Object arg1, Object arg2) {
			this.target = target; this.method = method;
			args = new Object[2];
			args[0] = arg1; args[1] = arg2;
		}
		
		@Override
		public void run() {
			Model.script.pcall(target, method, args);
		}
	}
	
	public static class Command extends Callback {
		public UiCommand command = null;
		
		public Command(UiCommand cmd) {
			command = cmd;
		}
		
		@Override
		public void run() {
			command.run();
		}
	}
	

	@Override
	public void run() {
	}
}