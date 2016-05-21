import shandalike.data.entity.Entity
import shandalike.data.behavior.Behavior
import shandalike.Util

def methodMissing(String name, args) {
	null
}

// This will populate the dungeon using information on the entrance entity.
// It will then replace the behavior with a dungeon transition behavior.
void collideWithPlayer(Behavior behavior, Entity dungeonEntrance, Entity playerPawn, arg2) {
	// Pass the rewards and encounters in to the dungeon spawn scripts as context.
	def context = [:]
	context.rewards = dungeonEntrance.getVar("rewards")
	context.encounters = dungeonEntrance.getVar("encounters")
	context.density = dungeonEntrance.getVar("density")
	context.dungeonEntranceMapId = Util.getActiveMapState().id
	context.dungeonEntranceEntityId = dungeonEntrance.id
	Util.setContext(context)
	println "dungeonPopulator: ${context}"

	// Create a new map ID for the dungeon
	String mapId = Util.generateID()

	// Add the standard dungeon entrance script
	dungeonEntrance.setVar("mapId", mapId)
	dungeonEntrance.collisionScript = "trigger_transitionToDungeon"

	// Run the entrance script
	Util.runScript("trigger_transitionToDungeon", "collideWithPlayer", behavior, dungeonEntrance, playerPawn, arg2)

	// Reward allocator has to run AFTER the map initializes.
	Util.runScript("dungeonRewardAllocator", "allocateRewards", context.rewards)
}
