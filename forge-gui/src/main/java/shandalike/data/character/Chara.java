package shandalike.data.character;

import shandalike.data.behavior.Behavior;
import shandalike.data.behavior.Behaviors;
import shandalike.data.behavior.IBehavioral;
import shandalike.data.entity.Pawn;

import java.util.Collection;

public class Chara implements IBehavioral {
	// Character's name
	public String name;
	// Current equipment
	public Equipment equipment;
	// Active modifiers (buffs/debuffs)
	public Behaviors behaviors;
	
	public Chara() {
		behaviors = new Behaviors();
	}
	
	/**
	 * If this character has a Pawn, locate it.
	 * @return
	 */
	public Pawn getPawn() {
		return null;
	}

	@Override
	public void addBehavior(Behavior behavior) {
		behaviors.add(behavior);
		behavior.handleEvent("behaviorDidAdd", this, null, null);
	}

	@Override
	public void removeBehavior(Behavior behavior) {
		behavior.handleEvent("behaviorWillRemove", this, null, null);
		behaviors.remove(behavior);
	}

	@Override
	public Collection<Behavior> getBehaviors() {
		return behaviors;
	}

	@Override
	public void handleEvent(String eventType, Object arg1, Object arg2) {
		IBehavioral thisBeh = getPawn();
		if(thisBeh == null) thisBeh = this;
		for(Behavior beh: getBehaviors()) {
			beh.handleEvent(eventType, thisBeh, arg1, arg2);
		}	
	}
}
