package shandalike.data.character;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Function;

import forge.card.MagicColor.Color;
import forge.deck.CardPool;
import forge.deck.Deck;
import forge.deck.DeckProxy;
import forge.deck.DeckSection;
import forge.game.GameType;
import forge.item.InventoryItem;
import forge.item.PaperCard;
import forge.util.storage.IStorage;
import forge.util.storage.StorageBase;
import shandalike.Model;
import shandalike.data.World;
import shandalike.mtg.RandomPool;

public class Inventory {
	// Amount of currency Player is carrying.
	private Map<String, Long> currency;
	// Player's card inventory
	final public CardPool cardPool = new CardPool();
	final CardPool newCards = new CardPool();
	// Player's deck collection
	public Map<String, Deck> decks;
	// Player's currently active deck
	public String activeDeckName;
	
	public Inventory() {
	}
	
	/**
	 * Add a card to the inventory.
	 * 
	 * @param card
	 * @param qty
	 */
	public void addCard(final PaperCard card, int qty) {
		cardPool.add(card, qty);
		newCards.add(card, qty);
		Model.gameEvent("playerInventoryChanged", null, null);
	}
	
	/**
	 * Remove a single card from the inventory. Also removes it from any decks if necessary.
	 * @param card
	 * @param qty
	 */
	public void removeCard(final PaperCard card, int qty) {
		// Remove card from pool
		cardPool.remove(card, qty);
		
		// Remove card from all decks (from QuestUtilCards)
		int remaining = cardPool.count(card);
		for(Deck deck: decks.values()) {
            int cntInMain = deck.getMain().count(card);
            int cntInSb = deck.has(DeckSection.Sideboard) ? deck.get(DeckSection.Sideboard).count(card) : 0;
            int nToRemoveFromThisDeck = cntInMain + cntInSb - remaining;
            if (nToRemoveFromThisDeck <= 0) continue;
            
            int nToRemoveFromSb = Math.min(cntInSb, nToRemoveFromThisDeck);
            if (nToRemoveFromSb > 0) {
                deck.get(DeckSection.Sideboard).remove(card, nToRemoveFromSb);
                nToRemoveFromThisDeck -= nToRemoveFromSb;
                if (0 >= nToRemoveFromThisDeck) continue;
            }
            
            deck.getMain().remove(card, nToRemoveFromThisDeck);
		}
		
		Model.gameEvent("playerInventoryChanged", null, null);
	}
	
	/**
	 * Add a collection of cards to the inventory.
	 * @param newCards
	 */
	public void addAllCards(final Iterable<PaperCard> newCards) {
		for(final PaperCard card: newCards) {
			addCard(card, 1);
		}
	}
	
	/**
	 * Remove a collection of cards from the inventory.
	 * @param cards
	 */
	public void removeAllCards(final Iterable<PaperCard> cards) {
		for(final PaperCard card: cards) {
			removeCard(card, 1);
		}
	}
	
	/**
	 * Get the amount of the given type currency the user has.
	 * @param currencyType
	 * @return
	 */
	public long getCurrency(String currencyType) {
		Long i = currency.get(currencyType);
		if(i == null) return 0;
		return i;
	}
	
	public boolean takeCurrency(String currencyType, long amt) {
		long x = getCurrency(currencyType);
		if(x < amt) return false;
		currency.put(currencyType, x - amt);
		Model.gameEvent("playerInventoryChanged", null, null);
		return true;
	}
	
	public void addCurrency(String currencyType, long amt) {
		currency.put(currencyType, getCurrency(currencyType) + amt);
		Model.gameEvent("playerInventoryChanged", null, null);
	}
	
	public static Inventory createInitialInventory(World world, int difficultyLevel, Color primaryColor) {
		Inventory inventory = new Inventory();
		world.hydrate();
		World.DifficultySpec difficulty = world.difficulties[difficultyLevel];
		
		// Initial currency
		inventory.currency = new HashMap<String,Long>();
		for(Entry<String, Long> e: difficulty.startingCurrency.entrySet()) {
			inventory.currency.put(e.getKey(), e.getValue());
		}
		inventory.currency.put("amulet_" + primaryColor.getName(), (long)difficulty.startingAmulets);
		primaryColor.getName();
		
		// Initial cardpool
		List<PaperCard> cards = RandomPool.generate(difficulty.startingPool, world.getFormat(), primaryColor);
		inventory.addAllCards(cards);
		
		// Initial empty deck list
		inventory.decks = new HashMap<String,Deck>();
		
		return inventory;
	}
	
	/**
	 * Set the active deck for the player.
	 * @param name
	 */
	public void setActiveDeckName(String name) {
		System.out.println("[Shandalike] setActiveDeck: " + name);
		// XXX: check that deck exists
		Model.gameEvent("playerInventoryChanged", null, null);
		activeDeckName = name;
	}
	
	/**
	 * @return The player's active deck.
	 */
	public Deck getActiveDeck() {
		if(activeDeckName == null) return null;
		return decks.get(activeDeckName);
	}
	
	//////////////////////
	// IStorage implementation to interface with Forge's deck editing
	public class DeckStorage extends StorageBase<Deck> {
		public DeckStorage(Inventory inv) {
			super("Shandalike decks", null, inv.decks);
		}
		
	    @Override
	    public void add(final Deck deck) {
	        this.map.put(deck.getName(), deck);
	    }

	    @Override
	    public void delete(final String deckName) {
	        this.map.remove(deckName);
	    }
	    
	    public List<DeckProxy> getDeckProxies() {
	    	List<DeckProxy> deckProxies = new ArrayList<DeckProxy>();
	    	for(Deck deck: this) {
	    		deckProxies.add(new DeckProxy(deck, "Shandalike", GameType.Shandalike, this));
	    	}
	    	return deckProxies;
	    }
	}
	
	public List<DeckProxy> getDeckProxies() {
		return (new DeckStorage(this)).getDeckProxies();
	}

	public IStorage<Deck> getDeckStorage() {
		return new DeckStorage(this);
	}
	
	//////////////////////////////////////////
	// "New cards" implementation like Forge quest mode
    public CardPool getNewCards() {
    	return newCards;
    }

    public void resetNewList() {
    	newCards.clear();
    }

    public boolean isNew(InventoryItem item) {
    	PaperCard pc = null;
    	try {
    		pc = (PaperCard)item;
    	} catch(ClassCastException ex) {
    		return false;
    	}
        return newCards.contains(pc);
    }

    public final transient Function<Entry<InventoryItem, Integer>, Comparable<?>> fnNewCompare =
            new Function<Entry<InventoryItem, Integer>, Comparable<?>>() {
        @Override
        public Comparable<?> apply(final Entry<InventoryItem, Integer> from) {
            return isNew(from.getKey()) ? Integer.valueOf(1) : Integer.valueOf(0);
        }
    };

    public final transient Function<Entry<? extends InventoryItem, Integer>, Object> fnNewGet =
            new Function<Entry<? extends InventoryItem, Integer>, Object>() {
        @Override
        public Object apply(final Entry<? extends InventoryItem, Integer> from) {
            return isNew(from.getKey()) ? "NEW" : "";
        }
    };
	
}
