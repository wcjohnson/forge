import shandalike.data.behavior.Behavior
import shandalike.data.behavior.IBehavioral
import shandalike.mtg.Duel
import lib.BuffUtils

// Every behavior must implement methodMissing.
def methodMissing(String name, args) { null }

// Get display-related information about this buff.
boolean isHidden(beh) { BuffUtils.getHideOverride(beh, false) }
boolean isHelpful(beh) { false }
String getTitle(beh) { BuffUtils.getStackedTitle(beh, "Life Total Carries Over") }
String getDescription(beh) {
	def lifeTotal = beh.getVar("lifeTotal")
	if(lifeTotal) {
		"Life: ${lifeTotal}"
	} else {
		"Life: ??"
	}
}

// When debuff is applied...
void behaviorDidAdd(Behavior beh, IBehavioral toWhom, arg1, arg2) {
	// If I already have a life total set, return
	if(beh.getVar("lifeTotal")) return
	// Compute base life total that the player would have had.
	// Do this by creating a fake duel and running all the player's buffs through it...
	Duel nonceDuel = new Duel()
	nonceDuel.prepare()
	toWhom.handleEvent("duelSetup", nonceDuel, nonceDuel.playerStart)
	// Then extract the life total.
	beh.setVar("lifeTotal", nonceDuel.playerStart.life)
}

// When player enters duel with this buff on...
void duelSetup(Behavior beh, IBehavioral toWhom, duel, playerStart) {
	def lifeTotal = beh.getVar("lifeTotal")
	if(lifeTotal) {
		// Force his life to the specified total.
		playerStart.forceLife = new Integer(lifeTotal)
	}
}

// When player exits duel with this buff on...
void duelEnded(Behavior beh, IBehavioral behavioral, duel, result) {
	BuffUtils.removeDuelStack(beh, behavioral)
	if(result.playerLifeTotal > 0) {
		// Store the total in the buff's vars
		beh.setVar("lifeTotal", result.playerLifeTotal)
	}
}