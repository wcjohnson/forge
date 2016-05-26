import shandalike.data.world.MapState
import shandalike.data.entity.PointOfInterest
import shandalike.data.behavior.Behavior
import shandalike.Util
import groovy.transform.Field

@Field def ll = null

class Lairs {
	def lairList = []

	public PointOfInterest random(String color) {
		return lairList[Util.randomInt(lairList.size())].deepCopy()
	}

	public void add(PointOfInterest poi) {
		lairList.add(poi)
	}
}

void buildLairs() {
	ll = new Lairs()

	def randomCardLair = new PointOfInterest()
	randomCardLair.showOnMinimap = false
	randomCardLair.labelOnMinimap = false
	randomCardLair.labelOnMap = true
	randomCardLair.label = "lair"
	randomCardLair.spriteAsset = "cave.sprite.json"
	randomCardLair.load()
	randomCardLair.setVar("rewards", [ [type: "card", amount: 1, minValue: 1, maxValue: 1000] ])
	randomCardLair.addBehavior(new Behavior("trigger_lair"))

	ll.add(randomCardLair)
}

Lairs getLairs() {
	if(ll == null) buildLairs()
	return ll
}
