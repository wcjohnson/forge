import shandalike.data.entity.Entity
import shandalike.data.behavior.Behavior
import shandalike.Util
import shandalike.UIModel

def methodMissing(String name, args) {
	null
}

void clickedReturn(arg1, arg2) {
	Util.popUI()
}

void doRewardCards(Entity playerPawn, rewardCards, boolean showUI) {
	// Add cards to player flag
	def rewards = playerPawn.getVar("dungeonRewards")
	def resolvedCards = []
	if(!rewards) {
		rewards = ["cards": [], "currency": []]
		playerPawn.setVar("dungeonRewards", rewards)
	}
	rewardCards.each {
		def card = Util.getFormat().getCardByName(it)
		if(card) {
			resolvedCards.push(card)
			rewards.cards.push(it)
		}
	}
	// Show the user some UI indicating that he got the card.
	if(showUI) {
		def ui = new UIModel()
		ui.addPanel("Reward!", "If you survive the dungeon, you will get:", this)
		ui.addCards("These Cards:", "", resolvedCards)
		ui.addButton("Return", this, "clickedReturn", null, null)
		Util.pushUI(ui)
	}
}

// Dungeon reward script.
// This adds a reward to the player's character record, which will be permanentized when
// the trigger_awardDungeonRewards is executed.
// Then it despawns the entity.
void collideWithPlayer(Behavior behavior, Entity trigger, Entity playerPawn, arg2) {
	// Convert reward cards to forge PaperCards
	def rewardCards = trigger.getVar("rewardCards")
	// Despawn this entity
	Util.getActiveMapState().removeEntity(trigger)
	// Call rewards
	if(rewardCards) doRewardCards(playerPawn, rewardCards, true)
}
