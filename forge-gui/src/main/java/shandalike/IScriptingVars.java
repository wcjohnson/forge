package shandalike;

/**
 * Interface for objects that support scripting variables.
 * @author wcj
 */
public interface IScriptingVars {
	public void setVar(String key, Object value);
	public Object getVar(String key);
}
