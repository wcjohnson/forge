package shandalike.mtg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import forge.deck.Deck;
import forge.game.card.Card;
import forge.game.card.CardCollectionView;
import forge.game.player.Player;
import forge.item.IPaperCard;
import forge.item.PaperCard;
import forge.util.Aggregates;

public class AnteBuilder {
	/** The final ante */
	public List<IPaperCard> ante = new ArrayList<IPaperCard>();
	/** The original pool */
	public List<IPaperCard> pool = new ArrayList<IPaperCard>();
	/** The intermediate builder state */
	private List<IPaperCard> builder;
	/** The target ante size */
	public int anteSize = 1;
	
	public AnteBuilder(Deck deck) {
		for(Entry<PaperCard, Integer> c: deck.getMain()) {
			for(int i=0; i<c.getValue(); i++) {
				pool.add(c.getKey());
			}
		}
		clear();
	}
	
	public AnteBuilder(CardCollectionView library) {
		for(Card card: library) {
			pool.add(card.getPaperCard());
		}
		clear();
	}
	
	/** Clear the ante, restoring the pool */
	public void clear() {
		builder = new ArrayList<IPaperCard>();
		for(IPaperCard pc: pool) builder.add(pc);
		ante.clear();
	}
	
	/** Add a random card to the ante */
	public boolean addRandom() {
		// Find a card other than a basic land
		Predicate<IPaperCard> antePredicate = new Predicate<IPaperCard>() {
			@Override
			public boolean apply(IPaperCard arg0) {
				return !arg0.getRules().getType().isBasicLand();
			}
		};
		// Add it to ante
		IPaperCard anted = Aggregates.random(Iterables.filter(builder, antePredicate));
		ante.add(anted);
		builder.remove(anted);
		return true;
	}
	
	/** Add a named card to the ante if possible */
	public boolean addByName(final String cardName) {
		// Find the card
		Predicate<IPaperCard> antePredicate = new Predicate<IPaperCard>() {
			@Override
			public boolean apply(IPaperCard arg0) {
				return arg0.getRules().getName().equals(cardName);
			}
		};
		IPaperCard anted = Iterables.find(builder, antePredicate, null);
		if(anted != null) {
			ante.add(anted);
			builder.remove(anted);
			return true;
		} else {
			return false;
		}
	}
	
	/** Fill ante with random cards */
	public void fill() {
		int i=0;
		while(ante.size() < anteSize && i < 100) {
			addRandom();
			i++;
		}
	}
	
	public static boolean matchPaperCardAnte(Player thisPlayer, CardCollectionView library, List<IPaperCard> ante, Multimap<Player,Card> antes) {
		// Defensive copy
		List<IPaperCard> anteMatcher = new ArrayList<IPaperCard>();
		anteMatcher.addAll(ante);
		for(Card c: library) {
			Iterator<IPaperCard> it = anteMatcher.iterator();
			while(it.hasNext()) {
				IPaperCard pc = it.next();
				if( 
						(c.getPaperCard().getName().equals(pc.getName())) && 
						(c.getPaperCard().getEdition().equals(pc.getEdition())) 
				) {
					antes.put(thisPlayer, c);
					it.remove();
					break;
				}
			}
		}
		return true;
	}
	
}