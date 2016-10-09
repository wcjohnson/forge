package shandalike.data.reward;

import shandalike.UIModel;
import shandalike.Util;

public class CurrencyReward implements Reward {
	public String type = "";
	public long amount = 0;
	
	public CurrencyReward(String type, int amount) {
		this.type = type; this.amount = amount;
	}
	
	@Override
	public String getDescription() {
		return amount + " " + type;
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
		Util.getPlayerInventory().addCurrency(type, amount);
	}

	@Override
	public void show(UIModel ui, boolean showPicker) {
		ui.addPanel("Reward!", getDescription());
	}

}
