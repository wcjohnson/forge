package shandalike.mtg;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

import forge.card.CardRules;
import forge.card.CardRulesPredicates;
import forge.card.MagicColor.Color;
import forge.item.IPaperCard.Predicates.Presets;
import forge.item.PaperCard;
import forge.quest.BoosterUtils;

/**
 * Construct initial pool from format and adventure data.
 * @author wcj
 */
public class RandomPool {
	public static class Mix {
		/** Relative integer frequency of on-color, mono-colored cards. */
		int onColorMonocolored = 3;
		/** Relative integer frequency of on-color cards which can be either mono- or multi-colored. */
		int onColor = 1;
		/** Relative integer frequency of off-color cards. */
		int offColor = 1;
		/** Relative integer frequency of colorless cards. */
		int colorless = 1;
	}
	
	public static class Spec {
		/** Number of cards at each rarity in pool. */
		public int mythic = 0, rare = 0, uncommon = 0, common = 40, basic = 20;
		/** If mythic is set to 0, chance a rare converts to a mythic. (0-1 basis) */
		public float rareToMythicChance = 0.0125f;
		/** Value cap on each card, as determined by Format.getCardValue(). 0 = uncapped. */
		public int valueCap = 0;
		public int valueMin = 0;
		/** Mix parameters. For every #onColorMix on-color cards added to the pool, #offcolorMix off color
		 * cards will be added, and {@link #colorlessMix} colorless cards will be added.
		 */
		public Mix mix = new Mix();
	}
	
	public static List<PaperCard> generate(final Spec spec, final Format format, Color primaryColor) {
		// Construct color filters.
		Predicate<CardRules> onColorMonocoloredFilter = CardRulesPredicates.isMonoColor(primaryColor.getColormask());
		Predicate<CardRules> onColorFilter = CardRulesPredicates.hasColor(primaryColor.getColormask());
		Predicate<CardRules> offColorFilter = null;
		for(Color c: Color.values()) {
			if(c != primaryColor && c != Color.COLORLESS) {
				if(offColorFilter == null) {
					offColorFilter = CardRulesPredicates.hasColor(c.getColormask());
				} else {
					offColorFilter = Predicates.or(offColorFilter, CardRulesPredicates.hasColor(c.getColormask()));
				}
			}
		}
		Predicate<CardRules> colorlessFilter = CardRulesPredicates.isMonoColor(Color.COLORLESS.getColormask());
		
		// Construct color filter sequence in proportions given by the card mix
		List<Predicate<CardRules>> colorFilterSequence = new ArrayList<Predicate<CardRules>>();
		for(int i=0;i<spec.mix.onColorMonocolored;i++) colorFilterSequence.add(onColorMonocoloredFilter);
		for(int i=0;i<spec.mix.onColor;i++) colorFilterSequence.add(onColorFilter);
		for(int i=0;i<spec.mix.offColor;i++) colorFilterSequence.add(offColorFilter);
		for(int i=0;i<spec.mix.colorless;i++) colorFilterSequence.add(colorlessFilter);
		
		// Construct rarity and value filters
		Predicate<PaperCard> valueFilter = Predicates.alwaysTrue();
		int valueCap = spec.valueCap;
		if(valueCap == 0) valueCap = Integer.MAX_VALUE;
		valueFilter = new Predicate<PaperCard>() {
			@Override
			public boolean apply(PaperCard input) {
				int val = format.getCardValue(input);
				return (val >= spec.valueMin && val <= spec.valueCap);
			}
		};
		Predicate<PaperCard> commonFilter = Predicates.and(Presets.IS_COMMON, valueFilter);
		Predicate<PaperCard> uncommonFilter = Predicates.and(Presets.IS_UNCOMMON, valueFilter);
		Predicate<PaperCard> rareFilter = Predicates.and(Presets.IS_RARE, valueFilter);
		Predicate<PaperCard> mythicFilter = Predicates.and(Presets.IS_MYTHIC_RARE, valueFilter);
		
		// Get core card pool
		List<PaperCard> cardPool = format.getAllCards();
		boolean hasMythics = Iterables.any(cardPool, Presets.IS_MYTHIC_RARE);
		
		// Generate random pool.
		List<PaperCard> cards = new ArrayList<>();
		cards.addAll(BoosterUtils.generateCards(cardPool, commonFilter, spec.common, colorFilterSequence, true));
		cards.addAll(BoosterUtils.generateCards(cardPool, uncommonFilter, spec.uncommon, colorFilterSequence, true));
		cards.addAll(BoosterUtils.generateCards(cardPool, rareFilter, spec.rare, colorFilterSequence, true));
		if(hasMythics) {
			cards.addAll(BoosterUtils.generateCards(cardPool, mythicFilter, spec.mythic, colorFilterSequence, true));
		}
		
		return cards;
	}
}
