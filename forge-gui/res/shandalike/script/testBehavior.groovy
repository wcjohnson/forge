import shandalike.data.behavior.Behavior
import shandalike.data.behavior.IBehavioral
import lib.BuffUtils

// Every behavior must implement methodMissing.
def methodMissing(String name, args) { null }

// Get display-related information about this buff.
boolean isHidden(beh) { BuffUtils.getHideOverride(beh, false) }
boolean isHelpful(beh) { true }
String getTitle(beh) { BuffUtils.getStackedTitle(beh, "Test Behavior") }
String getDescription(beh) { "Party time." }

// When a duel ends, deduct a stack of the buff if it has stacks.
def duelEnded(Behavior behavior, IBehavioral behavioral, arg1, arg2) {
	BuffUtils.removeDuelStack(behavior, behavioral)
}