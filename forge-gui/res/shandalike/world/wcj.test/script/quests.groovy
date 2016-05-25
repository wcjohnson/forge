import shandalike.data.entity.town.Town
import shandalike.Util
import shandalike.data.behavior.Behavior

import groovy.transform.Field

// Makes a travel quest asking player to travel to a random town within the
// searchRadius.
Behavior makeTravelQuest(Town town, float searchRadius) {
	// Locate possible towns.
	def nearbyTowns = Util.getActiveMapState().getAllEntities().findAll {
		(it instanceof Town) && !(it.id.equals(town.id)) && (it.distanceFrom(town) < searchRadius)
	}
	if(nearbyTowns.size() == 0) return null
	// Pick random one
	Town nearbyTown = nearbyTowns[ Util.randomInt(nearbyTowns.size()) ]
	// Create behavior data
	Behavior beh = new Behavior("objective_travel")
	beh.setVar("destinationName", nearbyTown.getName())
	beh.setVar("destinationId", nearbyTown.id)
	return beh
}

Behavior getRandomQuest(Town town) {
	return makeTravelQuest(town, 25.0f)
}
