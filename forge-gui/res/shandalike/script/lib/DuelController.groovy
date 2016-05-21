package lib
import shandalike.mtg.Duel
import shandalike.Model
import shandalike.Util

class DuelController {
	Duel duel
	def config = [:]
	int bribeValue = 0

	DuelController() {
		// Create Shanadalike java duel object
		duel = new Duel()
		duel.scriptDelegate = this
	}

	// We need this for script delegation
	def methodMissing(String name, args) { null }

	boolean startDuel() {
		println "DuelController.startDuel ${config}"
		duel.loadForgeDuelFile(config.duelFile)
		duel.prepare()
		if(duel.canDuel()) {
			duel.duel()
			return true
		} else {
			return false
		}
	}

	/////////////////// Preduel activities
	def duel_setup(theDuel) {
		computeAntes()
		buildDuelMenu()
	}

	def computeAntes() {
		// Default ante builder.
		duel.playerStart.buildAnte()
		duel.aiStart.buildAnte()
	}

	def buildDuelMenu() {
		buildBasicsMenu()
		buildBribeMenu()
		buildAnteMenu()
		buildCheatMenu()
		duel.menu.addButton("Duel!", this, "clickedStart", null, null)
	}

	def buildBasicsMenu() {
		duel.menu.addPanel("You are dueling ${duel.getOpponentName()}", "You will begin with ${duel.playerStart.getFinalLife()} life. ${duel.getOpponentName()} will begin with ${duel.aiStart.getFinalLife()} life.", this)
	}

	def buildBribeMenu() {
		if(config.canBribe) {
			bribeValue = config.bribeValue
			def bribeText = ""
			def canBribe = (Model.getPlayer().getInventory().getCurrency("gold") >= bribeValue)
			if(canBribe) {
				bribeText = "${duel.getOpponentName()} looks like the sticky-fingered sort. You think a bribe of ${bribeValue} Gold will persuade ${duel.getOpponentName()} to look the other way while you escape."
				duel.menu.addPanel("Bribe", bribeText, this, ["Bribe", "clickedBribe", null] as Object[])
			} else {
				bribeText = "${duel.getOpponentName()} looks like he might take a bribe of ${bribeValue} Gold to leave you alone. Unfortunately, you don't have the cash. To battle!"
				duel.menu.addPanel("Bribe", bribeText, this)
			}
		}
	}

	def buildCheatMenu() {
		if(Util.isCheatEnabled()) {
			duel.menu.addPanel("Cheat: Escape", "Because you are a DIRTY CHEATER, you can escape from this duel without consequences.", this, ["Escape", "clickedEscape", null] as Object[])
		}
	}

	def buildAnteMenu() {
		duel.menu.addCards("Your Ante", "", duel.playerStart.ante)
		duel.menu.addCards("Opponent Ante", "", duel.aiStart.ante)
	}

	def clickedBribe(arg1, arg2) {
		if(Model.getPlayer().getInventory().takeCurrency("gold", bribeValue)) {
			duel.cancel()
		}
	}

	void clickedEscape(arg1, arg2) {
		despawn()
		duel.cancel()
	}

	def clickedStart(arg1, arg2) {
		duel.startGame()
	}

	///////////////////// Postduel activities
	def duel_ended(theDuel) {
		buildPostDuelMenu()
		// Despawn entity if called for
		if( (config.despawnOnWin && duel.result.playerDidWin) || (config.despawnOnLose && duel.result.playerDidLose) ) {
			despawn()
		}
	}

	void despawn() {
		if(config.entityId) {
			def mapState = Util.getActiveMapState()
			def entity = mapState.getEntity(config.entityId)
			mapState.removeEntityById(config.entityId)
			// Also despawn nearby entities flagged as "trash" so user does not get in multiple duels back to back
			mapState.getAllEntities().each {
				if(it.getVar("trash") && it.distanceFrom(entity) < 2.0f) {
					mapState.removeEntity(it)
				}
			}
		}
	}

	def buildPostDuelMenu() {
		if(duel.result.playerDidWin) {
			duel.menu.addPanel("Victory!", "You have vanquished ${duel.getOpponentName()}!", this)
		} else {
			duel.menu.addPanel("Defeat!", "You were shamed by ${duel.getOpponentName()}", this)
		}
		duel.menu.addButton("Return to World", this, "clickedReturn", null, null)
	}

	def clickedReturn(arg1, arg2) {
		duel.cancel()
	}


}