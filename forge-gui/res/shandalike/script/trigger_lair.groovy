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

public void grantAwards(Entity lair, arg2) {
	def rewardDescs = lair.getVar("rewards")
	println "trigger_lair.grantAwards ${rewardDescs}"
	def rewards = rewardDescs.collect {
		def reward = RewardController.fromDescriptor(it)
		reward.build(); reward.award()
		return reward
	}
	// Show ui
	def ui = new UIModel()
	ui.addHeading("Lair Rewards!")
	rewards.each { it.show(ui, true) }
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
		// XXX: this is essentially copypasta of trigger_encounter
		// Factor all this out somewhere...
		def encs = trigger.getVar("encounters")
		def eid = encs[Util.randomInt(encs.size())]
		def encounters = Util.runScript("encounters", "getEncounters")
		def encounter = encounters.getById(eid)
		if(encounter == null) {
			println("[Shandalike] ERROR: no such encounter ${eid}.")
			return
		}
		def ctrl = encounter.makeDuelController()
		ctrl.config.canBribe = false
		ctrl.config.rewards = trigger.getVar("rewards")
		ctrl.startDuel()
	}
	// Despawn this entity
	Util.getActiveMapState().removeEntity(trigger)
}
