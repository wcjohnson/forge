import shandalike.Util
import shandalike.data.world.MapState
import shandalike.data.entity.Entity
import shandalike.data.entity.MobilePawn

// Spawn a random encounter from this dungeon's context.
void mapInit(float x, float y, float width, float height, MapState mapState, Map var) {
	def context = Util.getContext()
	println "dungeonEncounter spawner: ${context}"
	if( Util.randomFloat() < (float)context.density ) {
		// PIck random encounter
		String randomEncounter = context.encounters[Util.randomInt(context.encounters.size())]
		// Instantiate template
		Entity ent = Util.getWorldState().getBaseWorld().entityTemplates.get(randomEncounter)
		MobilePawn pawn = (MobilePawn)ent.deepCopy()
		pawn.pos.x = x; pawn.pos.y = y
		pawn.moving = false
		pawn.setVar("noBribe", true) // don't allow dungeon mobs to be bribed
		// Spawn pawn on map
		pawn.load()
		mapState.addEntity(pawn)
	}
}