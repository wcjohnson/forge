package lib

import shandalike.Util
import shandalike.data.reward.Reward
import shandalike.data.reward.CardReward
import shandalike.data.reward.CurrencyReward
import shandalike.UIModel

// Manages reward handouts.
class RewardController {

	static Reward fromDescriptor(Map descriptor) {
		int amt = 0
		String description = ""
		if(descriptor.minAmount) {
			int imin = (int)descriptor.minAmount
			int imax = (int)descriptor.maxAmount
			amt = Util.randomInt(imax - imin) + imin
			description += "${imin}-${imax}"
		} else if(descriptor.amount) {
			amt = descriptor.amount
			description += amt
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
				description += " (max value " + descriptor.maxValue + ")"
			}
			reward.description = description
			return reward
		}

		return null
	}

	static String describeRewardsFromDescriptors(descrs) {
		if(descrs == null || descrs.size() == 0) return "None.<br>\n"
		String desc = ""
		descrs.each {
			Reward r = RewardController.fromDescriptor(it)
			desc += "- ${r.getDescription()}<br>\n"
		}
		desc
	}

	static void grantAwardsFromDescriptors(descrs, UIModel ui) {
		def rewards = descrs.collect {
			def reward = RewardController.fromDescriptor(it)
			reward.build(); reward.award()
			reward
		}
		if(ui != null) {
			rewards.each{ it.show(ui, true) }
		}
	}

}
