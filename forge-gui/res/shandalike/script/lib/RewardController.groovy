// Manages reward handouts.
class RewardController {
	// Structure specifying the reward.
	// Fields:
	// cards[] - List of specific string card names to award.

	def rewardSpec
	// Card rewards, resolved to Forge PaperCard objects
	def resolvedCards = []
	// Currency rewards
	def currency = [:]

	RewardController(rs) {
		rewardSpec = rs
	}

	void process() {
		if(rewardSpec.cards) {
			rewardSpec.cards.each {
				def card = Util.getFormat().getCardByName(it)
				if(card) {
					resolvedCards.push(card)
				}
			}
		}
	}


}
