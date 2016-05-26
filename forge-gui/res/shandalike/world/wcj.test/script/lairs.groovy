import shandalike.data.world.MapState
import shandalike.data.entity.PointOfInterest
import groovy.transform.Field

@Field def lairs = null

class Lairs {
	def lairs = []
}

void buildLairs() {
	lairs = new Lairs()

	def randomCardLair =
}

Lairs getLairs() {
	if(lairs == null) buildLairs()
	return lairs
}
