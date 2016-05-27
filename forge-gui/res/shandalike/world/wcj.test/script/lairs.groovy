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
	//ll.add(buildRandomCardLair())
	//ll.add(buildQuizLair())
	ll.add(buildDuelLair1())
}

Lairs getLairs() {
	if(ll == null) buildLairs()
	return ll
}
