package shandalike.data.reward;

import java.util.Map;
import java.util.Map.Entry;

import shandalike.Util;

public class CurrencyReward implements Reward {
	
	public Map<String,Long> awards;

	@Override
	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		for(Entry<String, Long> kv: awards.entrySet()) {
			sb.append(kv.getValue());
			sb.append(" ");
			sb.append(kv.getKey());
		}
		return sb.toString();
	}

	@Override
	public void build() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean requiresChoice() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void choose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void award() {
		for(Entry<String, Long> kv: awards.entrySet()) {
			Util.getPlayerInventory().addCurrency(kv.getKey(), kv.getValue());
		}
	}

}
