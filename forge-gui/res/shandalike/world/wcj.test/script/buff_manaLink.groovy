import shandalike.data.behavior.Behavior
import shandalike.data.behavior.IBehavioral
import shandalike.mtg.Duel
import lib.BuffUtils

// Every behavior must implement methodMissing.
def methodMissing(String name, args) { null }

// Get display-related information about this buff.
boolean isHidden(beh) { BuffUtils.getHideOverride(beh, true) }
boolean isHelpful(beh) { true }
String getTitle(beh) { BuffUtils.getStackedTitle(beh, "Mana Link") }
String getDescription(beh) { "Starting Life total +1" }

// When player enters duel with this buff on...
void duelSetup(Behavior beh, IBehavioral toWhom, duel, playerStart) {
	playerStart.life = playerStart.life + 1
}

// When player exits duel with this buff on...
void duelEnded(Behavior beh, IBehavioral behavioral, duel, result) {
	BuffUtils.removeDuelStack(beh, behavioral)
}
