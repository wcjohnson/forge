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
		} else {
			amt = descriptor.amount
		}

		if(descriptor.type.equals("currency")) {
			return new CurrencyReward(descriptor.currency, amt)
		}

		if(descriptor.type.equals("card")) {
			def reward = new CardReward()
			reward.n = amt
			String description = "" + amt
			if(descriptor.duplicate) {
				reward.setDuplicateCard();
				description += " duplicate"
			}
			if(descriptor.color) {
				reward.filterColor(descriptor.color)
				description += " " + descriptor.color
			}
			description += " cards"
			if(descriptor.maxValue) {
				reward.filterValue(descriptor.minValue, descriptor.maxValue)
				description += "(max value " + descriptor.maxValue + ")"
			}
			reward.description = description
			return reward
		}

		return null
	}

}
