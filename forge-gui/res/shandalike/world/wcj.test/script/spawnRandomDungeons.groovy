import shandalike.Util
import shandalike.data.world.MapState
import shandalike.data.entity.PointOfInterest
import groovy.transform.Field

@Field MapState mapState
@Field String terrain

// Find position to put this dungeon
void locateDungeon(PointOfInterest dungeon, int tries) {
	float x = Util.randomFloat() * (float)mapState.getMapInfo().width;
	float y = Util.randomFloat() * (float)mapState.getMapInfo().height
	terrain = mapState.getMapInfo().getTerrainAt(x, y)
	if(terrain == null || terrain.equals("none")) {
		locateDungeon(dungeon, tries + 1)
		return
	}
	println "Locating dungeon on terrain ${terrain}"
	dungeon.pos.x = x
	dungeon.pos.y = y
}

void setupDungeon(PointOfInterest dungeon) {
	locateDungeon(dungeon, 0)
	dungeon.showOnMinimap = false
	dungeon.labelOnMinimap = false
	dungeon.labelOnMap = true
	dungeon.label = "Dungeon"
	dungeon.spriteAsset = "cave.sprite.json"
	dungeon.load()
	dungeon.setVar("baseMap", "dungeon_1")
	dungeon.setVar("rewards", [])
	dungeon.setVar("encounters", ["black_trash_1"])
	dungeon.setVar("behaviors", [ ["script": "debuff_lifeTotalCarriesOver"], ["script": "debuff_evictedOnLoss"] ])
	dungeon.setVar("density", 0.5)
	dungeon.collisionScript = "dungeonPopulator"
}

void mapInit(float x, float y, float width, float height, MapState ms, Map var) {
	mapState = ms
	// Get list of dungeon reward cards
	def worldState = Util.getWorldState()
	def rewardCards = worldState.getBaseWorld().rewardOnly;
	def currentDungeon = null;
	int cardsInCurrentDungeon = 0;
	for(int i = 0; i < rewardCards.length; i++) {
		if(currentDungeon == null) {
			println "Spawning a new dungeon..."
			currentDungeon = mapState.addEntity(PointOfInterest.class)
			setupDungeon(currentDungeon)
		}
		println "Adding ${rewardCards[i]} to dungeon"
		currentDungeon.getVar("rewards").push(rewardCards[i])
		cardsInCurrentDungeon++
		if(cardsInCurrentDungeon >= 3) {
			currentDungeon = null; cardsInCurrentDungeon = 0
		}
	}
}
