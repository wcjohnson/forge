package lib
import shandalike.data.entity.town.Town
import shandalike.Model
import shandalike.UIModel
import shandalike.Util
import shandalike.data.behavior.Behavior

class TownController {
	Town town
	UIModel townMenu
	long foodPrice
	long foodQty = 20
	def offeredQuests = []

	TownController(Town ts) {
		this.town = ts
		townMenu = new UIModel()
	}

	// We need this for script delegation
	def methodMissing(String name, args) { null }

	void enter() {
		// IMPORTANT: ALWAYS delegate to the engine first when leaving/entering towns.
		town.enter()
		// Handle script events
		Util.getPlayer().handleEvent("playerDidEnterTown", town, null)
		// Perform pre-entry tasks.
		doCardShopMaintenance()
		buildTownMenu()
		// Offer scripts the opportunity to enhance the town menu
		Util.getPlayer().handleEvent("townWillBuildMenu", town, townMenu)
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
		buildQuestMenu()
		buildLeaveTownMenu()
		townMenu.update()
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

	void buildQuestMenu() {
		townMenu.addButton("Quests", this, "doQuests", null, null)
	}

	/////////////////// Callbacks
	void doQuests(arg1, arg2) {
		def questUI = new UIModel()
		def quest = Util.runScript("quests", "getRandomQuest", town)
		quest.runScript("buildUI", Util.getPlayer(), questUI, "offer")
		questUI.addButton("Back to Town", this, "doExitSubscreen", null, null)
		Util.pushUI(questUI)
	}

	void doExitSubscreen(arg1, arg2) {
		Util.popUI()
	}

	void doBuyFood(arg1, arg2) {
		if(Util.getPlayerInventory().takeCurrency("gold", foodPrice)) {
			Util.getPlayerInventory().addCurrency("food", foodQty)
		}
	}

	void openCardShop(arg1, arg2) {
		if(town.cardShop != null) {
			// Instruct the core to open the card shop tab with the town's shop model
			Util.openShop(town.cardShop.getShopModel())
		}
	}

	void openDecks(arg1, arg2) {
		Util.openDecks()
	}

	void tryLeaveTown(arg1, arg2) {
		// Make sure player has an active, legal deck.
		def deck = Util.getPlayerInventory().getActiveDeck()
		if(deck == null) {
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
