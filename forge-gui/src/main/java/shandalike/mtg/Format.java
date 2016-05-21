/*
 * Forge: Play Magic: the Gathering.
 * Copyright (C) 2011  Forge Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package shandalike.mtg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;

import forge.card.CardEdition;
import forge.game.GameFormat;
import forge.item.PaperCard;
import forge.model.FModel;


/**
 * Card format for a Shandalike world.
 */
public final class Format extends GameFormat {
	public static class Spec {
		public ArrayList<String> sets;
		public ArrayList<String> bans;
		public Map<String,Integer> rarityValues;
		public Map<String,Integer> cardValues;
	}
	
	public Map<String,Integer> cardValues;
	public Map<String,Integer> rarityValues;
	transient List<PaperCard> allCards = null;
    
    public Format(String name, Spec spec) {
    	super(name, spec.sets, spec.bans);
    	cardValues = spec.cardValues;
    	rarityValues = spec.rarityValues;
    }
    
    public int getRarityValue(String rarityName) {
    	Integer i = rarityValues.get(rarityName);
    	if(i == null) return 0; else return i;
    }
    
    public int getCardValue(PaperCard card) {
    	String name = card.getName();
    	Integer i = cardValues.get(name);
    	if(i != null) return i;
    	// Get default value by rarity
    	return getRarityValue(card.getRarity().getLongName());
    }
    
    // Cache the allCards list
    @Override
	public List<PaperCard> getAllCards() {
    	if(allCards != null) return allCards;
    	allCards = super.getAllCards();
    	return allCards;
	}
    
    public PaperCard getCardByName(String name) {
    	for(PaperCard c: this.getAllCards()) {
    		if(c.getName().equals(name)) return c;
    	}
    	return null;
    }

	/**
     * Checks if the current format contains sets with snow-land (horrible hack...).
     * @return boolean, contains snow-land sets.
     * 
     */
    public boolean hasSnowLands() {
        return (this.isSetLegal("ICE") || this.isSetLegal("CSP"));
    }

    /**
     * The Class Predicates.
     */
    public abstract static class Predicates {
        /**
         * Checks if is legal in quest format.
         *
         * @param qFormat the format
         * @return the predicate
         */
        public static Predicate<CardEdition> isLegalInFormatQuest(final Format qFormat) {
            return new LegalInFormatQuest(qFormat);
        }

      private static class LegalInFormatQuest implements Predicate<CardEdition> {
        private final Format qFormat;

        public LegalInFormatQuest(final Format fmt) {
            this.qFormat = fmt;
        }

        @Override
        public boolean apply(final CardEdition subject) {
            return this.qFormat.isSetLegal(subject.getCode());
        }
      }
    }
    
    /**
     * Implementation of Forge's QuestController.getDefaultLandSet() -- used by deck editor to Add Basic Land
     * to deck.
     * @return
     */
    public CardEdition getDefaultLandSet() {
    	List<String> availableEditionCodes = this.getAllowedSetCodes();
    	List<CardEdition> availableEditions = new ArrayList<CardEdition>();
    	for(String s: availableEditionCodes) {
    		availableEditions.add(FModel.getMagicDb().getEditions().get(s));
    	}
    	CardEdition randomLandSet = CardEdition.Predicates.getRandomSetWithAllBasicLands(availableEditions);
    	return randomLandSet == null ? FModel.getMagicDb().getEditions().get("ZEN") : randomLandSet;
    }
}