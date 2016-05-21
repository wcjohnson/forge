package lib
import shandalike.data.behavior.Behavior
import shandalike.data.behavior.IBehavioral

class BuffUtils {

	static boolean getHideOverride(Behavior behavior, boolean hideDefault) {
		def hide = behavior.getVar("hide")
		if(hide != null) {
			(boolean)hide
		} else {
			hideDefault
		}
	}

	// Modify title of a buff to show # of stacks.
	static String getStackedTitle(Behavior behavior, String unstackedTitle) {
		def durDuels = behavior.getVar("durDuels")
		if(durDuels != null) {
			int stax = durDuels;
			if(stax == 1)
				return "${unstackedTitle} (1 duel)"
			else
				return "${unstackedTitle} (${stax} duels)"
		} else {
			return unstackedTitle
		}
	}


	// Remove a stack of the buff after a duel is over, if it has a duration
	static void removeDuelStack(Behavior behavior, IBehavioral behavioral) {
		def durDuels = behavior.getVar("durDuels")
		if(durDuels == null) return
		int stax = durDuels - 1
		if(stax <= 0) {
			behavioral.removeBehavior(behavior)
		} else {
			behavior.setVar("durDuels", stax)
		}
	}
}