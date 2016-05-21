package forge.ai.ability;


import forge.ai.SpellAbilityAi;
import forge.game.GlobalRuleChange;
import forge.game.card.Card;
import forge.game.card.CardState;
import forge.game.card.CounterType;
import forge.game.player.Player;
import forge.game.spellability.SpellAbility;
import forge.game.zone.ZoneType;

public class SetStateAi extends SpellAbilityAi {
    @Override
    protected boolean checkApiLogic(final Player aiPlayer, final SpellAbility sa) {
        final Card source = sa.getHostCard();

        if (!source.hasAlternateState()) {
            System.err.println("Warning: SetState without ALTERNATE on " + source.getName() + ".");
            return false;
        }

        // Prevent transform into legendary creature if copy already exists
        // Check first if Legend Rule does still apply
        if (!aiPlayer.getGame().getStaticEffects().getGlobalRuleChange(GlobalRuleChange.noLegendRule)) {

            // check if the other side is legendary and if such Card already is in Play
            final CardState other = source.getAlternateState();

            if (other != null && other.getType().isLegendary() && aiPlayer.isCardInPlay(other.getName())) {
                if (!other.getType().isCreature()) {
                    return false;
                }

                final Card othercard = aiPlayer.getCardsIn(ZoneType.Battlefield, other.getName()).getFirst();
                
                // for legendary KI counter creatures
                if (othercard.getCounters(CounterType.KI) >= source.getCounters(CounterType.KI)) {
                	// if the other legendary is useless try to replace it
                    if (!isUselessCreature(aiPlayer, othercard)) {                    	
                        return false;
                    }
                }
            }
        }
        
        if (sa.getTargetRestrictions() == null && "Transform".equals(sa.getParam("Mode"))) {
            return !source.hasKeyword("CARDNAME can't transform");
        }
        if ("Flip".equals(sa.getParam("Mode"))) {
        	return true;
        }
        return false;
    }

    @Override
    public boolean chkAIDrawback(SpellAbility sa, Player aiPlayer) {
        // Gross generalization, but this always considers alternate
        // states more powerful
        return !sa.getHostCard().isInAlternateState();
    }
}
