import shandalike.Util
import shandalike.data.world.MapState
import shandalike.data.entity.Entity
import shandalike.data.entity.MobilePawn
import shandalike.data.entity.PlayerPawn
import groovy.transform.Field

@Field MapState mapState
@Field PlayerPawn playerPawn
@Field String trashTileTerrain
@Field float x
@Field float y
@Field boolean failed

// Velocity multiplier to offset spawn center along direction of player motion.
// This is used to make the trash spawn "in front of" the player where he is more
// likely to encounter it.
@Field float forwardMultiplier = 5.0f
// Radius in which trash is eligible to spawn in
@Field float spawnRadius = 15.0f
// Radius in which a spawned trash would be considered too close to the player
@Field float minRadius = 4.0f
// The chance of trash from a different terrain appearing instead of terrain-appropriate trash
@Field float wandererChance = 0.25f
// Amount of time in seconds until a trash despawns if unengaged.
@Field float despawnTime = 60.0f

void position(int tries) {
	// Prevent pathology.
	if(tries > 50) {
		failed = true
		return
	}
	// Preferentially spawn trash in the direction the player is walking
	x = playerPawn.pos.x + playerPawn.velocity.x * forwardMultiplier;
	y = playerPawn.pos.y + playerPawn.velocity.y * forwardMultiplier;
	// Locate random position for trash spawning
	x = x + (Util.randomFloat() - 0.5f) * 2.0f * spawnRadius
	y = y + (Util.randomFloat() - 0.5f) * 2.0f * spawnRadius
	// Don't spawn too close
	if(playerPawn.distanceFrom(x,y) < minRadius) {
		position(tries + 1)
		return
	}
	// Get terrain of trash. Don't spawn on no terrain
	trashTileTerrain = mapState.getMapInfo().getTerrainAt(x,y)
	if(!trashTileTerrain || trashTileTerrain.equals("none")) {
		position(tries + 1)
		return
	}
}

void reify() {
	def encounters = Util.runScript("encounters", "getEncounters")
	def encounter = encounters.random(trashTileTerrain, wandererChance)
	if(encounter == null) { failed = true; return }
	// Make the pawn
	MobilePawn pawn = encounter
	pawn.pos.x = x; pawn.pos.y = y
	pawn.setVar("trash", true)
	pawn.setVar("despawnAt", Util.getGameTime() + despawnTime)
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

// Make more trash mobs
void spawnMore(trash) {
	for(int i=trash.size(); i < 10; i++) {
		println("[Shandalike] Spawning trash #${i}")
		make()
	}
}

// Cleanup faraway and expired trash mobs.
void cleanup(trash) {
	// Despawn all those that are too far away
	def deadTrash = trash.findAll { it.distanceFrom(playerPawn) > (spawnRadius * 1.5f) }
	deadTrash.each {
		println "Removing trash ${it.id} because of range"
		mapState.removeEntity(it)
		trash.remove(it)
	}
	// Despawn all those that are expired for time
	float t = Util.getGameTime()
	deadTrash = trash.findAll { it.getVar("despawnAt") < t }
	deadTrash.each {
		println "Removing trash ${it.id} because of despawn time"
		mapState.removeEntity(it)
		trash.remove(it)
	}
}

void timer(ent) {
	mapState = Util.getActiveMapState()
	playerPawn = mapState.getPlayerPawn()
	if(!playerPawn) return
	// Get all trash mobs
	def trash = mapState.getAllEntities().findAll { it instanceof MobilePawn && it.getVar("trash") }
	println "Existing trash: ${trash}"
	// Cleanup dead trash
	cleanup(trash)
	// Spawn more trash if needed
	spawnMore(trash)
}
