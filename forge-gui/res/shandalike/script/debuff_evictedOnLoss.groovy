import shandalike.data.behavior.Behavior
import shandalike.data.behavior.IBehavioral
import shandalike.Util
import lib.BuffUtils

// Every behavior must implement methodMissing.
def methodMissing(String name, args) { null }

// Get display-related information about this buff.
boolean isHidden(beh) { BuffUtils.getHideOverride(beh, false) }
boolean isHelpful(beh) { false }
String getTitle(beh) { BuffUtils.getStackedTitle(beh, "Evicted on Loss") }
String getDescription(beh) {
	"Lose and you're out!"
}

// Eject the player
void evict() {
	Util.exitMap()
}

// When player exits duel with this buff on, he will be removed from the dungeon
// with no rewards.
void duelEnded(Behavior behavior, IBehavioral behavioral, duel, result) {
	BuffUtils.removeDuelStack(behavior, behavioral)
	if(!result.playerDidWin) {
		Util.getPlayer().getPawn().setVar("dungeonRewards", null)
		duel.menu.addPanel("Ejected in Shame!", "${duel.getOpponentName()} casts you out from the dungeon in shame.", this)
		evict()
	}
}