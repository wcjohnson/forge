import shandalike.data.entity.Entity
import shandalike.data.behavior.Behavior
import shandalike.Util
import shandalike.UIModel
import shandalike.Callback

import lib.RewardController
import lib.Quiz

def methodMissing(String name, args) {
	null
}

void clickedReturn(arg1, arg2) {
	Util.popUI()
}

void doAcceptDuel(Entity lair, def encounter) {
	Util.popUI()
	def ctrl = encounter.makeDuelController()
	ctrl.config.canBribe = false
	ctrl.config.rewards = lair.getVar("rewards")
	ctrl.startDuel()
}

void grantAwards(Entity lair, arg2) {
	def rewardDescs = lair.getVar("rewards")
	def ui = new UIModel()
	ui.addHeading("Lair Rewards!")
	RewardController.grantAwardsFromDescriptors(rewardDescs, ui)
	ui.addButton("Return", this, "clickedReturn", null, null)
	Util.pushUI(ui)
}

// Dungeon reward script.
// This adds a reward to the player's character record, which will be permanentized when
// the trigger_awardDungeonRewards is executed.
// Then it despawns the entity.
void collideWithPlayer(Behavior behavior, Entity trigger, Entity playerPawn, arg2) {
	String lairType = trigger.getVar("lairType")
	if(lairType.equals("quiz")) {
		Callback cb = new Callback.ScriptObject(this, "grantAwards", trigger, null)
		Quiz quiz = new Quiz()
		quiz.onCorrect = cb
		quiz.generate()
		quiz.show()
	} else if(lairType.equals("reward")) {
		grantAwards(lair, null)
	} else if (lairType.equals("duel")) {
		def encs = trigger.getVar("encounters")
		def eid = encs[Util.randomInt(encs.size())]
		def encounters = Util.runScript("encounters", "getEncounters")
		def encounter = encounters.getById(eid)
		if(encounter == null) {
			println("[Shandalike] ERROR: no such encounter ${eid}.")
			return
		}
		// Duel prompt
		def ui = new UIModel()
		ui.addPanel("${encounter.name} Lair", """<html>
You stumbled on a ${encounter.name} lair.<br>
You may flee, or fight the beast for the following rewards:<br><br>\n
""" + RewardController.describeRewardsFromDescriptors(trigger.getVar("rewards")) + "</html>"
		)
		ui.addButton("Fight", this, "doAcceptDuel", trigger, encounter)
		ui.addButton("Flee", this, "clickedReturn", null, null)
		Util.pushUI(ui)
	}
	// Despawn this entity
	Util.getActiveMapState().removeEntity(trigger)
}
