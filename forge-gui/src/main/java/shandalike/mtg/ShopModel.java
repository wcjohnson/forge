package shandalike.mtg;

import java.util.Map.Entry;

import forge.item.InventoryItem;
import forge.util.ItemPool;

/**
 * Model for a card shop. Can be specialized and fed into a single controller powered by the Forge Spell Shop interface.
 * Used to implement Shandalike "shop"-type screens, including the shops in towns, bazaars, "dupe" events, "any card"
 * events, "pick n rewards" events, etc.
 * @author wcj
 */
public interface ShopModel {
	/**
	 * Result of a buy or sell transaction.
	 * @author wcj
	 */
	public static class TransactionResult {
		public ItemPool<InventoryItem> itemsInventory;
		public ItemPool<InventoryItem> itemsShop;
		public boolean transactionComplete = false;
		public long totalCurrencyTransferredToShop = 0;
		public String errorMessage = "No items changed hands.";
	}
	/** Get the player's inventory. */
	public ItemPool<InventoryItem> getPlayerInventory();
	/** Get the shop's inventory */
	public ItemPool<InventoryItem> getShopInventory();
	/** Get currency available to the player */
	public long getPlayerCurrency();
	/** Get currency available to the shop. */
	public long getShopCurrency();
	/** GEt name of relevant currency of this shop. gold, amulets, etc. */
	public String getCurrencyName();
	/** Attempt to take the given amount of currency from the player, returning true if successful. DO NOT use on dry runs. */
	public boolean takePlayerCurrency(long amt);
	public boolean givePlayerCurrency(long amt);
	public boolean takeShopCurrency(long amt);
	public boolean giveShopCurrency(long amt);
	/** Can this shop buy cards from the player? */
	public boolean canBuy();
	/** Can the player sell cards to this shop? */
	public boolean canSell();
	/**
	 * Perform a buy.
	 * @param items List of items to buy from the shop.
	 * @param dryRun If true, transaction will not be executed, just checked for errors.
	 * @return
	 */
	public ShopModel.TransactionResult buy(Iterable<Entry<InventoryItem, Integer>> items, boolean dryRun);
	/**
	 * Perform a sell.
	 * @param items List of items to sell to the shop.
	 * @param dryRun If true, transaction will not be executed, just checked for errors.
	 * @return
	 */
	public ShopModel.TransactionResult sell(Iterable<Entry<InventoryItem, Integer>> items, boolean dryRun);
	/** Get price to buy cards */
	public long getBuyPrice(InventoryItem item, Integer count);
	/** Get price to sell cards */
	public long getSellPrice(InventoryItem item, Integer count);
	/** Get qty owned by player. */
	public int getQtyOwned(InventoryItem item);
}