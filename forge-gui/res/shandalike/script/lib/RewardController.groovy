package lib

import shandalike.Util
import shandalike.data.reward.Reward
import shandalike.data.reward.CardReward
import shandalike.data.reward.CurrencyReward

// Manages reward handouts.
class RewardController {

	static Reward fromDescriptor(Map descriptor) {
		int amt = 0
		if(descriptor.minAmount) {
			amt = Util.randomInt(descriptor.maxAmount - descriptor.minAmount) + descriptor.minAmount
		} else if(descriptor.amount) {
			amt = descriptor.amount
		}

		if(descriptor.type.equals("currency")) {
			return new CurrencyReward(descriptor.currency, amt)
		}

		if(descriptor.type.equals("card")) {
			def reward = new CardReward()
			// Named card reward
			if(descriptor.cards) {
				descriptor.cards.each { reward.addNamedCard(it) }
				reward.description = reward.cards.join(', ')
				return reward
			}
			// Unnamed reward
			reward.n = amt
			String description = "" + amt
			if(descriptor.pick) {
				reward.setPicked()
				description += " choice of"
			} else {
				description += " random"
			}
			if(descriptor.duplicate) {
				reward.setDuplicateCard();
				description += " duplicate"
			}
			if(descriptor.color) {
				reward.filterColor(descriptor.color)
				description += " " + descriptor.color
			} else {
				description += " any"
			}
			description += " cards"
			if(descriptor.maxValue) {
				reward.filterValue((int)descriptor.minValue, (int)descriptor.maxValue)
				description += "(max value " + descriptor.maxValue + ")"
			}
			reward.description = description
			return reward
		}

		return null
	}

}
