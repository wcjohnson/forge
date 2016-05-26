import shandalike.Util
import shandalike.data.world.MapState
import shandalike.data.entity.Entity
import shandalike.data.entity.MobilePawn
import shandalike.data.entity.PlayerPawn
import shandalike.data.entity.PointOfInterest
import groovy.transform.Field

@Field MapState mapState
@Field PlayerPawn playerPawn
@Field String tileTerrain
@Field float x
@Field float y
@Field boolean failed

@Field int max = 10

void position(int tries) {
	// Prevent pathology.
	if(tries > 50) {
		failed = true
		return
	}
	// Get map dimensions
	def mapInfo = mapState.getMapInfo()
	// Locate random position for trash spawning
	x = Util.randomFloat() * (float)(mapInfo.width)
	y = Util.randomFloat() * (float)(mapInfo.height)
	// Get terrain. Don't spawn on no terrain
	tileTerrain = mapInfo.getTerrainAt(x,y)
	println "Positioning lair at ${x} ${y} on terrain ${tileTerrain}"
	if(!tileTerrain || tileTerrain.equals("none")) {
		position(tries + 1)
		return
	}
}

void reify() {
	def lairs = Util.runScript("lairs", "getLairs")
	def lair = lairs.random(tileTerrain)
	if(lair == null) { failed = true; return }
	// Make the pawn
	PointOfInterest pawn = lair
	pawn.pos.x = x; pawn.pos.y = y
	pawn.setVar("lair", true)
	// Spawn pawn on map
	pawn.load()
	mapState.addEntity(pawn)
}

void make() {
	failed = false
	position(0)
	if(failed) return
	reify()
}

// Make more
void spawnMore(stuff) {
	for(int i=stuff.size(); i < max; i++) {
		println("[Shandalike] Spawning lair #${i}")
		make()
	}
}

// Cleanup faraway and expired
void cleanup(stuff) {

}

void timer(ent) {
	println "SpawnLairs"
	mapState = Util.getActiveMapState()
	playerPawn = mapState.getPlayerPawn()
	if(!playerPawn) return
	// Get all entities
	def stuff = mapState.getAllEntities().findAll { it instanceof PointOfInterest && it.getVar("lair") }
	println "Existing lairs: ${stuff}"
	// Cleanup dead trash
	cleanup(stuff)
	// Spawn more trash if needed
	spawnMore(stuff)
}
