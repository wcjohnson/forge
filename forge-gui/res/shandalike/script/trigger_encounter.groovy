import shandalike.Util
import shandalike.data.entity.Entity
import shandalike.data.behavior.Behavior

import lib.DuelController

// Every Behavior script must provide methodMissing, which will be called when your script
// does not handle the specific event that was triggered. Generally you will do nothing here.
def methodMissing(String name, args) {
	return null;
}

// Specific game events will call the method with the same name as the event name.
// The first parameter for every event is the behavior, and the second is the entity
// implementing hte behavior. The remaining args are event specific. If you do not
// know the type of the arg, leave it typeless, as incorrectly typing it will cause
// your method not to be called by Groovy.
def collideWithPlayer(Behavior behavior, Entity thisEntity, Entity playerPawn, arg2) {
	// Determine encounter.
	String eid = (String)thisEntity.getVar("encounterId")
	if(eid == null) {
		eid = (String)behavior.getVar("encounterId")
	}
	if(eid == null) {
		println("[Shandalike] ERROR: trigger_encounter: no encounterId defined.")
		return
	}
	def encounters = Util.runScript("encounters", "getEncounters")
	def encounter = encounters.getById(eid)
	if(encounter == null) {
		println("[Shandalike] ERROR: trigger_encounter: no such encounter ${eid}.")
		return
	}

	def ctrl = encounter.makeDuelController()
	if(!thisEntity.getVar("noBribe")) {
		ctrl.config.canBribe = true
	} else {
		ctrl.config.canBribe = false
	}
	ctrl.config.entityId = thisEntity.id
	ctrl.config.despawnOnWin = true
	ctrl.startDuel()
}
