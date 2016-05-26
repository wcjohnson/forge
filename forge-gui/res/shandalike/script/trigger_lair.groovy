import shandalike.data.entity.Entity
import shandalike.data.behavior.Behavior
import shandalike.Util
import shandalike.UIModel

import lib.RewardController

def methodMissing(String name, args) {
	null
}

void clickedReturn(arg1, arg2) {
	Util.popUI()
}

// Dungeon reward script.
// This adds a reward to the player's character record, which will be permanentized when
// the trigger_awardDungeonRewards is executed.
// Then it despawns the entity.
void collideWithPlayer(Behavior behavior, Entity trigger, Entity playerPawn, arg2) {
	def rewardDescs = trigger.getVar("rewards")
	println "trigger_lair ${rewardDescs}"
	def rewards = rewardDescs.collect {
		def reward = RewardController.fromDescriptor(it)
		reward.build(); reward.award()
		return reward
	}
	// Show ui
	def ui = new UIModel()
	rewards.each { it.show(ui, true) }
	ui.addButton("Return", this, "clickedReturn", null, null)
	Util.pushUI(ui)
	// Despawn this entity
	Util.getActiveMapState().removeEntity(trigger)
}
