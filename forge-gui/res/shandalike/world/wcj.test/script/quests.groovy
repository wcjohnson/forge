import shandalike.data.entity.town.Town
import shandalike.Util
import shandalike.data.behavior.Behavior

import groovy.transform.Field

// Makes a travel quest asking player to travel to a random town within the
// searchRadius.
Behavior makeTravelQuest(Town town, float searchRadius, int difficulty) {
	// Locate possible towns.
	def nearbyTowns = Util.getActiveMapState().getAllEntities().findAll {
		(it instanceof Town) && !(it.id.equals(town.id)) && (it.distanceFrom(town) < searchRadius)
	}
	if(nearbyTowns.size() == 0) {
		println "quests: No nearby towns within ${searchRadius}..."
		return null
	}
	// Pick random one
	Town nearbyTown = nearbyTowns[ Util.randomInt(nearbyTowns.size()) ]
	// Create behavior data
	Behavior beh = new Behavior("objective_travel")
	beh.setVar("destinationName", nearbyTown.getName())
	beh.setVar("destinationId", nearbyTown.id)
	// Generate rewards
	def rewards = [
		[type: "currency", currency: "gold", amount: (50 / (difficulty + 1))]
	]
	// Random amulet chance
	if(Util.randomFloat() < 1.0f / (float)(difficulty + 1)) {
		String ammy = "amulet_${Util.randomColorName()}"
		rewards.add([type: "currency", currency: ammy, amount: 1])
	}
	beh.setVar("rewards", rewards)
	return beh
}

// Makes a kill quest asking player to slay a randomly selected mob type
Behavior makeKillQuest(Town town, int difficulty) {
	// Locate a random encounter
	def encounters = Util.runScript("encounters", "getEncounters")
	def encounter = encounters.randomEncounter()
	String encId = encounter.id
	// Create the behavior
	Behavior beh = new Behavior("objective_kill")
	beh.setVar("destinationName", town.getName())
	beh.setVar("destinationId", town.id)
	def targets = [:]
	targets[encId] = [0, 1]
	beh.setVar("targets", targets)
	// Generate rewards
	def rewards = [
		[type: "currency", currency: "gold", amount: (50 / (difficulty + 1))]
	]
	// Random amulet chance
	if(Util.randomFloat() < 1.0f / (float)(difficulty + 1)) {
		String ammy = "amulet_${Util.randomColorName()}"
		rewards.add([type: "currency", currency: ammy, amount: 1])
	}
	beh.setVar("rewards", rewards)
	return beh
}

Behavior getRandomQuest(Town town) {
	int difficulty = Util.getDifficulty()
	if(Util.randomFloat() < 0.5f) {
		return makeTravelQuest(town, 50.0f, difficulty)
	} else {
		return makeKillQuest(town, difficulty)
	}
}
