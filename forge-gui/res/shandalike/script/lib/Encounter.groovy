package lib

import shandalike.Util
import shandalike.data.entity.MobilePawn
import shandalike.data.behavior.Behavior

import lib.DuelController

class Encounter {
	float difficulty = 1.0f
	String id = ""
	String name = ""
	String category = ""
	String duelFile
	String sprite
	int baseBribe = 0

	Encounter(String id, String name, String category, String sprite, String duelFile, float difficulty) {
		this.id = id
		this.name = name
		this.category = category
		this.sprite = sprite
		this.duelFile = duelFile
		this.difficulty = difficulty
		this.baseBribe = difficulty * 50
	}

	// Prototype of this entity for cloning
	protected MobilePawn prototype = null

	protected void makePrototype() {
		prototype = new MobilePawn()
		// add encounter behavior
		Behavior beh = new Behavior("trigger_encounter")
		prototype.addBehavior(beh)
		// add duel file
		prototype.setVar("encounterName", name)
		prototype.setVar("encounterId", id)
		prototype.setVar("duelFile", duelFile)
		// add sprite
		prototype.spriteAsset = sprite
	}

	// Get duel-information for this encounter
	public DuelController makeDuelController() {
		DuelController dc = new DuelController()
		dc.config.duelFile = this.duelFile
		dc.config.encounterName = this.name
		dc.config.bribeValue = (int)((Util.randomFloat() + 0.5f) * (float)this.baseBribe)
		dc.duel.encounterId = this.id
		return dc
	}

	// Spawn an instance of this encounter
	public MobilePawn spawn() {
		if(prototype == null) makePrototype()
		return (MobilePawn)prototype.deepCopy()
	}


}
