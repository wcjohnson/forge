package shandalike.data.behavior;

import java.util.Collection;

/**
 * Interface for an object that has Behaviors -- scriptable points of interface between the object,
 * game events, and user script code.
 * @author wcj
 */
public interface IBehavioral {
	/**
	 * Add a behavior to this object. It is safe to use during iteration.
	 * @param behavior
	 */
	public void addBehavior(Behavior behavior);
	
	/**
	 * Remove a behavior from this object. It is safe to use this during iteration.
	 * @param behavior
	 */
	public void removeBehavior(Behavior behavior);
	
	/**
	 * Get all behaviors on this object.
	 * THE RETURNED COLLECTION SHOULD BE TREATED AS IMMUTABLE! Use add/removeBehavior
	 * to modify the behaviors.
	 * @return
	 */
	public Collection<Behavior> getBehaviors();
	
	/**
	 * Handle an event using the behaviors on this object.
	 * @param eventType
	 * @param arg1
	 * @param arg2
	 */
	public void handleEvent(String eventType, Object arg1, Object arg2);
}
