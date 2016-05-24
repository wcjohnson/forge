package shandalike.data.entity.town;

import java.util.Map.Entry;

import forge.card.MagicColor;
import forge.deck.CardPool;
import forge.item.InventoryItem;
import forge.item.PaperCard;
import forge.util.ItemPool;
import shandalike.Model;
import shandalike.Util;
import shandalike.data.character.Inventory;
import shandalike.mtg.RandomPool;
import shandalike.mtg.ShopModel;

public class CardShop {
	/** Amount of gold the shopkeeper has to buy cards. */
	public long gold = 0;
	/** Last time this shop was restocked. */
	public float lastStockTime = 0.0f;
	/** Time delay between restocks. */
	public float restockDelay = 60.0f;
	/** Fraction of card price when player buys card from shop */
	public float buyRatio = 1.0f;
	/** Fraction of card price when player sells card to shop */
	public float sellRatio = 0.25f;
	/** RandomPool data used for restocking */
	public RandomPool.Spec restockSpec;
	/** Amount of gold to add when this shop restocks. */
	public long restockGold = 0;
	/** Shop inventory */
	public CardPool inventory;
	
	/**
	 * Determine if the spell shop needs restocked.
	 * @param parentTown
	 * @return
	 */
	public boolean needsRestock(final Town parentTown) {
		if(inventory == null) return true;
		float t = Model.getGameTime();
		if(t - lastStockTime > restockDelay) return true;
		return false;
	}
	
	/**
	 * Restock the spell shop.
	 * @param parentTown
	 */
	public void restock(final Town parentTown) {
		if(inventory == null) inventory = new CardPool();
		inventory.clear();
		MagicColor.Color shopColor = Util.colorForName(parentTown.getMapState().getMapInfo().getTerrainAt(parentTown.pos.x, parentTown.pos.y));
		inventory.add(RandomPool.generate(restockSpec, Model.adventure.getWorld().getFormat(), shopColor));
		gold += restockGold;
		lastStockTime = Model.getGameTime();
	}
	
	/**
	 * Get the ShopModel to attach to the shop controller for this card shop.
	 * @param parentTown
	 * @return
	 */
	public ShopModel getShopModel() {
		return new ShopModel() {

			@Override
			public ItemPool<InventoryItem> getPlayerInventory() {
				return ItemPool.createFrom(Model.adventure.getPlayer().getInventory().cardPool, InventoryItem.class);
			}

			@Override
			public ItemPool<InventoryItem> getShopInventory() {
				return ItemPool.createFrom(inventory, InventoryItem.class);
			}

			@Override
			public long getPlayerCurrency() {
				return Model.adventure.getPlayer().getInventory().getCurrency("gold");
			}

			@Override
			public long getShopCurrency() {
				return gold;
			}

			@Override
			public String getCurrencyName() {
				return "Gold";
			}

			@Override
			public boolean canBuy() {
				return true;
			}

			@Override
			public boolean canSell() {
				return true;
			}

			@Override
			public TransactionResult buy(Iterable<Entry<InventoryItem, Integer>> items, boolean dryRun) {
				Inventory inventory = Model.adventure.getPlayer().getInventory();
				TransactionResult tr = new TransactionResult();
				
				// Tally legal buys and cost
		        long totalCost = 0;
		        ItemPool<InventoryItem> itemsToBuy = new ItemPool<>(InventoryItem.class);
		        for (Entry<InventoryItem, Integer> itemEntry : items) {
		            final InventoryItem item = itemEntry.getKey();
		            if (item instanceof PaperCard) {
		                final int qty = itemEntry.getValue();
		                itemsToBuy.add(item, qty);
		                totalCost += getBuyPrice(item, qty);
		            }
		        }
		        if (itemsToBuy.isEmpty()) { return tr; }
		        
		        long creditsShort = totalCost - getPlayerCurrency();
		        if(creditsShort > 0) {
		        	tr.totalCurrencyTransferredToShop = totalCost;
		        	tr.errorMessage = "You need " + creditsShort + " more " + getCurrencyName() + " to make this purchase.";
		        	return tr;
		        }
		        
		        if(dryRun) {
		        	tr.totalCurrencyTransferredToShop = totalCost;
		        	tr.transactionComplete = true;
		        	tr.errorMessage = null;
		        	return tr;
		        }
		        
		        // Add cards to player inventory.
		        ItemPool<InventoryItem> itemsToAdd = new ItemPool<>(InventoryItem.class);
		        for (Entry<InventoryItem, Integer> itemEntry : itemsToBuy) {
		            final InventoryItem item = itemEntry.getKey();
		            final int qty = itemEntry.getValue();
		            final long value = getBuyPrice(item, qty);
		            // Take gold from player
		            if(inventory.takeCurrency("gold", value)) {
		            	// Add card to player
		            	inventory.addCard((PaperCard)item, qty);
		            	itemsToAdd.add(item, qty);
		            	// Add the gold from the shop inventory
			            CardShop.this.gold += value;
			            // Rmoeve the card from the shop inventory
			            CardShop.this.inventory.remove((PaperCard)item, qty);
		            }
		        }

		        // Return successful transaction details
		        tr.totalCurrencyTransferredToShop = totalCost;
		        tr.transactionComplete = true;
		        tr.errorMessage = null;
		        tr.itemsShop = itemsToBuy;
		        tr.itemsInventory = itemsToAdd;
				return tr;
			}

			@Override
			public TransactionResult sell(Iterable<Entry<InventoryItem, Integer>> items, boolean dryRun) {
				Inventory inventory = Model.adventure.getPlayer().getInventory();
				TransactionResult tr = new TransactionResult();
				
		        long totalReceived = 0;
		        ItemPool<InventoryItem> itemsToSell = new ItemPool<>(InventoryItem.class);
		        for (Entry<InventoryItem, Integer> itemEntry : items) {
		            final InventoryItem item = itemEntry.getKey();
		            if (item instanceof PaperCard) {
		                final int qty = itemEntry.getValue();
		                itemsToSell.add(item, qty);
		                totalReceived += getSellPrice(item, qty);
		            }
		        }
		        if (itemsToSell.isEmpty()) { return tr; }
		        tr.totalCurrencyTransferredToShop = -totalReceived;
		        
		        long creditsShort = getShopCurrency() - totalReceived;
		        if(creditsShort < 0) {
		        	tr.errorMessage = "The shop does not have enough " + getCurrencyName() + " to buy these cards.";
		        	return tr;
		        }
	        	tr.transactionComplete = true;
	        	tr.errorMessage = null;
		        if(dryRun) {
		        	return tr;
		        }
		        
		        for (Entry<InventoryItem, Integer> itemEntry : itemsToSell) {
		            final PaperCard card = (PaperCard) itemEntry.getKey();
		            final int qty = itemEntry.getValue();
		            final long price = getSellPrice(card, qty);
		            
		            // Subtract the card from the player
		            inventory.removeCard(card, qty);
		            // Subtract the gold from the shop inventory
		            CardShop.this.gold -= price;
		            // Add the gold to the player inventory
		            inventory.addCurrency("gold", price);
		            // Add the card to the shop inventory
		            CardShop.this.inventory.add(card, qty);
		        }
		        
		        tr.itemsShop = itemsToSell;
		        tr.itemsInventory = itemsToSell;
		        return tr;
			}

			@Override
			public long getBuyPrice(InventoryItem item, Integer count) {
				float f = (float)Model.adventure.getWorld().getFormat().getCardValue((PaperCard)item) * (float)count * buyRatio;
				return (long)f;
			}

			@Override
			public long getSellPrice(InventoryItem item, Integer count) {
				float f = (float)Model.adventure.getWorld().getFormat().getCardValue((PaperCard)item) * (float)count * sellRatio;
				return (long)f;
			}

			@Override
			public int getQtyOwned(InventoryItem item) {
				return Model.adventure.getPlayer().getInventory().cardPool.count((PaperCard)item);
			}
			
		};
	}
}
