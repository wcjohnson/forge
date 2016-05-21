import shandalike.Util
import shandalike.data.behavior.Behavior

// On exit dungeon, purge all dungeon debuffs and award rewards
def methodMissing(String name, args) {
	null
}

void mapLeave(mapState) {
	println "exitDungeon"
	// Purge dungeon debuffs
	Behavior.purgeByTag(Util.getPlayer(), "dungeon")
	// Award reward cards
	Util.runScript("awardDungeonRewards", "mapLeave", mapState)
}
