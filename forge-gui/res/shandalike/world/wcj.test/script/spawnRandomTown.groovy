import shandalike.data.entity.town.Town
import shandalike.data.entity.town.CardShop
import shandalike.Util
import shandalike.Model
import shandalike.data.JSON
import shandalike.mtg.RandomPool
import shandalike.data.behavior.Behavior

String randomName(first, last) {
	String pickedFirst = first[Model.rng.randomInt(first.size())]
	String pickedLast = last[Model.rng.randomInt(last.size())]
	"${pickedFirst} ${pickedLast}"
}

void mapInit(float x, float y, float width, float height, mapState, Map var) {
	Town town = mapState.addEntity(Town.class)
	town.addBehavior(new Behavior("trigger_town"))
	town.pos.x = x; town.pos.y = y
	town.spriteAsset = "house.sprite.json"

	town.name = randomName([
		"Bloodsand",
		"Unicorn's",
		"Hornwall",
		"Eloren",
		"Sahrmal's",
		"Evos",
		"Kalonian",
		"Osai",
		"Valkas",
		"Mardrake"
	], [
		"Spire",
		"Village",
		"Hamlet",
		"Tower",
		"Bazaar",
		"Keep",
		"Steading",
		"Temple"
	])

	CardShop cardShop = new CardShop()
	RandomPool.Spec rps = new RandomPool.Spec()

	cardShop.gold = 100
	cardShop.restockDelay = 600.0f
	cardShop.restockGold = 50
	cardShop.restockSpec = rps
	cardShop.buyRatio = Util.getDifficultySpec().buyRatio
	cardShop.sellRatio = Util.getDifficultySpec().sellRatio

	rps.mythic = 0; rps.rare = 2; rps.uncommon = 5; rps.common = 15; rps.basic = 0
	rps.rareToMythicChance = 0.0125f
	rps.valueCap = 500
	rps.mix.onColorMonocolored = 2
	rps.mix.onColor = 2
	rps.mix.offColor = 1
	rps.mix.colorless = 1

	town.cardShop = cardShop

	println "[Shandalike] spawnRandomTown: spawned town named " + town.name
}
