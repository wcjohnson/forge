import shandalike.Util
import shandalike.data.world.MapState
import shandalike.data.world.MapInfo
import shandalike.data.entity.CollidablePawn

// Iterate over rewards in this dungeon's context; place at random reward points.
void allocateRewards(inRewards) {
	MapState ms = Util.getActiveMapState()
	// Defensive copy of the rewards struct.
	def rewards = []
	inRewards.each { rewards.push(it) }
	// All possible locations where a reward could go.
	def rewardSpawns = []
	ms.getMapInfo().getMetaObjectLayer().getObjects().each {
		if(it.getProperties()?.get("script")?.equals("dungeonReward")) {
			rewardSpawns.push(ms.getMapInfo().getObjectLocation(it))
		}
	}
	println "allocateRewards: allocating ${rewards} to ${rewardSpawns}"
	// Place as many rewards as we can
	while(rewards.size() > 0 && rewardSpawns.size() > 0) {
		// Pick location for reward
		def reward = rewards[0]
		def location = rewardSpawns[Util.randomInt(rewardSpawns.size())]

		// Make treasure chest at location
		def ent = ms.addEntity(CollidablePawn.class)
		ent.pos.x = location.x; ent.pos.y = location.y
		ent.spriteAsset = "chest.sprite.json"; ent.load()
		ent.setVar("rewardCards", [ reward ] )
		ent.collisionScript = "trigger_dungeonReward"

		// Remove from location
		println "allocateRewards: placed ${reward} at ${location}"
		rewards.remove(reward); rewardSpawns.remove(location)
	}
}