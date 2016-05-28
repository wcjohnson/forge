import shandalike.data.world.MapState
import shandalike.data.entity.PointOfInterest
import shandalike.data.behavior.Behavior
import shandalike.Util
import groovy.transform.Field

@Field def ll = null

class Lairs {
	def lairList = []

	public PointOfInterest random(String color) {
		return lairList[Util.randomInt(lairList.size())].deepCopy()
	}

	public void add(PointOfInterest poi) {
		lairList.add(poi)
	}
}

PointOfInterest buildRandomCardLair() {
	def lair = new PointOfInterest()
	lair.addBehavior(new Behavior("trigger_lair"))
	lair.labelOnMap = true
	lair.label = "RandomCardLair"
	lair.spriteAsset = "cave.sprite.json"
	lair.load()
	lair.setVar("lairType", "reward")
	lair.setVar("rewards", [
		[type: "card", amount: 1, minValue: 0, maxValue: 500]
	])

	lair
}

PointOfInterest buildQuizLair() {
	def lair = new PointOfInterest()
	lair.addBehavior(new Behavior("trigger_lair"))
	lair.labelOnMap = true
	lair.label = "QuizLair"
	lair.spriteAsset = "cave.sprite.json"
	lair.load()
	lair.setVar("lairType", "quiz")
	lair.setVar("rewards", [
		[type: "card", minAmount: 1, maxAmount: 3, minValue: 0, maxValue: 1000]
	])

	lair
}

PointOfInterest buildBazaarLair() {
	def lair = new PointOfInterest()
	lair.addBehavior(new Behavior("trigger_lair"))
	lair.labelOnMap = true
	lair.label = "BazaarLair"
	lair.spriteAsset = "cave.sprite.json"
	lair.load()
	lair.setVar("lairType", "shop")
	lair.setVar("shopName", "Bazaar of Wonders")
	lair.setVar("currencyType", "gold")
	lair.setVar("currencyRatio", 2.0f)
	lair.setVar("inventory", [type: "card", minValue: 0, maxValue: 10000, pick: true])

	lair
}

PointOfInterest buildGemLair() {
	def lair = new PointOfInterest()
	lair.addBehavior(new Behavior("trigger_lair"))
	lair.labelOnMap = true
	lair.label = "GemLair"
	lair.spriteAsset = "cave.sprite.json"
	lair.load()
	lair.setVar("lairType", "shop")
	lair.setVar("shopName", "Diamond Mine")
	lair.setVar("currencyType", "amulets")
	lair.setVar("currencyRatio", 1.0f/500.0f)
	lair.setVar("inventory", [type: "card", minValue: 0, maxValue: 20000, pick: true])

	lair
}

PointOfInterest buildDuelLair1() {
	def lair = new PointOfInterest()
	lair.addBehavior(new Behavior("trigger_lair"))
	lair.labelOnMap = true
	lair.label = "DuelLair1"
	lair.spriteAsset = "cave.sprite.json"
	lair.load()
	lair.setVar("lairType", "duel")
	lair.setVar("encounters", [ "u1", "b1", "r1", "g1" ])
	lair.setVar("rewards", [
		[type: "card", minAmount: 2, maxAmount: 5, minValue: 0, maxValue: 1500]
	])

	lair
}

void buildLairs() {
	ll = new Lairs()
	def rc = buildRandomCardLair()
	def q = buildQuizLair()
	def baz = buildBazaarLair()
	def gem = buildGemLair()
	def d1 = buildDuelLair1()
	int difficulty = Util.getDifficulty()

	ll.add(rc); ll.add(rc); ll.add(rc); ll.add(rc); ll.add(rc)
	ll.add(rc); ll.add(rc); ll.add(rc); ll.add(rc); ll.add(rc)
	ll.add(q); ll.add(q)
	ll.add(d1); ll.add(d1); ll.add(d1); ll.add(d1)

	// Lower difficulty = more bazzaars
	ll.add(baz)
	if(difficulty < 3) ll.add(baz)
	if(difficulty < 2) ll.add(baz)
	if(difficulty < 1) ll.add(baz)
	ll.add(gem)
	if(difficulty < 2) ll.add(gem)
}

Lairs getLairs() {
	if(ll == null) buildLairs()
	return ll
}
