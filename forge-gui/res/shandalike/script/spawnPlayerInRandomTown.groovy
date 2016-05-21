import shandalike.data.Adventure
import shandalike.data.entity.Entity
import shandalike.data.entity.town.Town
import shandalike.data.world.MapState
import shandalike.data.world.WorldState
import shandalike.Model


void mapFirstEnter(MapState mapState) {
	def list = mapState.getAllEntities().findAll { it instanceof Town }
	if(list.size() == 0) return
	def randomIndex = Model.rng.randomInt(list.size())
	def randomTown = list[randomIndex]
	mapState.inTownId = randomTown.id
	// Move player pawn to town center
	def playerPawn = mapState.getPlayerPawn()
	playerPawn.pos.x = randomTown.pos.x
	playerPawn.pos.y = randomTown.pos.y
	mapState.suppressPlayerCollision()
	println "Player randomly spawned in " + randomTown.name
}
