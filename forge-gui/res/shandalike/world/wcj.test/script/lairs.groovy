import shandalike.data.world.MapState
import shandalike.data.entity.PointOfInterest
import groovy.transform.Field

@Field def lairs = null

class Lairs {
	def lairs = []
}

void buildLairs() {
	lairs = new Lairs()

	def randomCardLair = new PointOfInterest()
	randomCardLair.showOnMinimap = false
	randomCardLair.labelOnMinimap = false
	randomCardLair.labelOnMap = true
	randomCardLair.label = "lair"
	randomCardLair.spriteAsset = "cave.sprite.json"
}

Lairs getLairs() {
	if(lairs == null) buildLairs()
	return lairs
}
