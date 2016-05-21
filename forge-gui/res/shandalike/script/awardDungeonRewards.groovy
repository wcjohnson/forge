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

void grantRewards(player, boolean showUI) {
	def rewards = player.getVar("dungeonRewards")
	def resolvedCards = []
	rewards.cards.each {
		def card = Util.getFormat().getCardByName(it)
		if(card) {
			resolvedCards.push(card)
			Util.getPlayerInventory().addCard(card, 1)
		}
	}
	if(showUI && (resolvedCards.size() > 0 )) {
		ui = new UIModel()
		ui.addPanel("Reward!", "You survived the dungeon! You got:", this)
		if(resolvedCards.size() > 0) {
			ui.addCards("These Cards:", "", resolvedCards)
		}
		ui.addButton("Return", this, "clickedReturn", null, null)
		Util.pushUI(ui)
	}

}

void mapLeave(mapState) {
	def player = mapState.getPlayerPawn()
	def rewards = player.getVar("dungeonRewards")
	if(rewards) {
		grantRewards(player, true)
	}
}
