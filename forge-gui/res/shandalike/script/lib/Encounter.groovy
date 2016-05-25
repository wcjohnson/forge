package lib

import shandalike.data.entity.MobilePawn
import shandalike.data.behavior.Behavior

class Encounter {
	float difficulty = 1.0f
	String category = ""
	String duelFile
	String sprite

	Encounter(String category, String sprite, String duelFile, float difficulty) {
		this.category = category
		this.sprite = sprite
		this.duelFile = duelFile
		this.difficulty = difficulty
	}

	// Prototype of this entity for cloning
	protected MobilePawn prototype = null

	protected void makePrototype() {
		prototype = new MobilePawn()
		// add encounter behavior
		Behavior beh = new Behavior("encounter")
		prototype.addBehavior(beh)
		// add duel file
		prototype.setVar("duelFile", duelFile)
		// add sprite
		prototype.spriteAsset = sprite
	}

	// Spawn an instance of this encounter
	public MobilePawn spawn() {
		if(prototype == null) makePrototype()
		return (MobilePawn)prototype.deepCopy()
	}


}
