package lib
import shandalike.data.entity.town.Town
import shandalike.Model
import shandalike.UIModel
import shandalike.Util

class TownController {
	Town town
	UIModel townMenu
	long foodPrice
	long foodQty = 20

	TownController(Town ts) {
		this.town = ts
		townMenu = new UIModel()
	}

	// We need this for script delegation
	def methodMissing(String name, args) { null }

	void enter() {
		// IMPORTANT: ALWAYS delegate to the engine first when leaving/entering towns.
		town.enter()
		// Perform pre-entry tasks.
		doCardShopMaintenance()
		buildTownMenu()
		Util.pushUI(townMenu)
	}

	void leave() {
		// IMPORTANT: ALWAYS delegate to the engine first when leaving a town.
		town.leave()
		Util.popUI()
	}

	/////////////////// Setup town menu
	void buildTownMenu() {
		townMenu.clear()
		buildTownTitle()
		buildCardShopMenu()
		buildEditDecksMenu()
		buildBuyFoodMenu()
		buildLeaveTownMenu()
	}

	void buildTownTitle() {
		townMenu.addPanel(town.getName(), "Welcome to ${town.getName()}!", this)
	}

	void buildCardShopMenu() {
		if(town.cardShop != null) {
			townMenu.addButton("Card Shop", this, "openCardShop", null, null)
		}
	}

	void buildEditDecksMenu() {
		townMenu.addButton("Edit Decks", this, "openDecks", null, null)
	}

	void buildLeaveTownMenu() {
		townMenu.addButton("Leave Town", this, "tryLeaveTown", null, null)
	}

	void buildBuyFoodMenu() {
		foodPrice = (long)( Util.getDifficultySpec().buyRatio * 33.0f )
		townMenu.addButton("Buy Food (${foodPrice} Gold for ${foodQty} food)", this, "doBuyFood", null, null)
	}

	/////////////////// Callbacks
	void doBuyFood(arg1, arg2) {
		if(Util.getPlayerInventory().takeCurrency("gold", foodPrice)) {
			Util.getPlayerInventory().addCurrency("food", foodQty)
		}
	}

	void openCardShop(arg1, arg2) {
		if(town.cardShop != null) {
			// Instruct the core to open the card shop tab with the town's shop model
			Util.openShop(town.cardShop.getShopModel(town))
		}
	}

	void openDecks(arg1, arg2) {
		Util.openDecks()
	}

	void tryLeaveTown(arg1, arg2) {
		// Make sure player has an active, legal deck.
		if(Util.getPlayer().getInventory().getActiveDeck() == null) {
			Util.showMessageBox("Cannot leave town without an active deck! Choose 'Edit Decks' to build one.", "No Active Deck")
			return
		}

		leave()
	}

	/////////////////// Misc
	void doCardShopMaintenance() {
		if(town.cardShop != null) {
			if(town.cardShop.needsRestock(town)) {
				town.cardShop.restock(town)
			}
		}
	}
}
