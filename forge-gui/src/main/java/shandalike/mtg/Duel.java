package shandalike.mtg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

import forge.FThreads;
import forge.GuiBase;
import forge.LobbyPlayer;
import forge.deck.Deck;
import forge.game.Game;
import forge.game.GameRules;
import forge.game.GameType;
import forge.game.card.Card;
import forge.game.card.CardCollectionView;
import forge.game.player.Player;
import forge.game.player.RegisteredPlayer;
import forge.game.zone.ZoneType;
import forge.interfaces.IGuiGame;
import forge.item.IPaperCard;
import forge.item.PaperCard;
import forge.match.HostedMatch;
import forge.model.FModel;
import forge.player.GamePlayerUtil;
import forge.properties.ForgePreferences.FPref;
import forge.quest.QuestEvent;
import forge.quest.QuestEventChallenge;
import forge.quest.QuestUtil;
import forge.quest.io.QuestChallengeReader;
import forge.quest.io.QuestDuelReader;
import forge.util.FileSection;
import forge.util.FileUtil;
import groovy.lang.GroovyObject;
import shandalike.UIModel;
import shandalike.Model;
import shandalike.data.entity.Entity;

/**
 * Interface to conduct duels in Forge and handle results.
 * @author wcj
 */
public class Duel implements GameRules.AnteDelegate {
	public static class StartData {
		/** Starting life */
		public int life = 20;
		/** Force life. If not null, life will be forced to this value at duel start */
		public Integer forceLife = null;
		/** Starting hand size */
		public int hand = 7;
		/** Number of cards to ante */
		public int anteSize = 1;
		/** Starting permanents on battlefield */
		public final List<IPaperCard> permanents = new ArrayList<IPaperCard>();
		/** Starting deck */
		public Deck deck;
		/** Starting ante. If null, will ante a random card. */
		public List<IPaperCard> ante;
		
		/** Add the given card to this player's battlefield as a permanent */
		public void addPermament(String cardName) {
			permanents.add(QuestUtil.readExtraCard(cardName));
		}
		
		public void buildAnte() {
			AnteBuilder ab = new AnteBuilder(deck);
			ab.anteSize = anteSize;
			ab.fill();
			ante = ab.ante;
		}
		
		public int getFinalLife() {
			if(forceLife != null) return forceLife; else return life;
		}
		
		public void applyToRegisteredPlayer(RegisteredPlayer rp) {
			getFinalLife();
			if(this.permanents != null) rp.setCardsOnBattlefield(this.permanents);
			rp.setStartingLife(Math.max(this.getFinalLife(), 1));
			rp.setStartingHand(Math.max(this.hand, 0));
		}
	}
	
	public static class Result {
		public int playerGamesWon = 0;
		public int aiGamesWon = 0;
		public int playerLifeTotal = 0;
		public int aiLifeTotal = 0;
		public boolean playerDidWin = false;
		public List<PaperCard> cardsWon = new ArrayList<PaperCard>();
		public List<PaperCard> cardsLost = new ArrayList<PaperCard>();
	}
	
	public static class GameResult {
		public int playerLifeTotal = 0;
		public int aiLifeTotal = 0;
		public boolean playerDidWin = false;
	}
	
	/** Starting situation of the player */
	public StartData playerStart = new StartData();
	/** Starting situation of the AI */
	public StartData aiStart = new StartData();
	/** Game entity associated with the AI. */
	public Entity aiEntity;
	/** Menu model for custom menu entries on Duel screen. */
	public UIModel menu = new UIModel();
	/** Result of duel */
	public Result result;
	/** Scripting delegate */
	public GroovyObject scriptDelegate;

	private RegisteredPlayer human;
	private RegisteredPlayer ai;
	LobbyPlayer humanLobbyPlayer;
	LobbyPlayer aiLobbyPlayer;
	// AI player info
	String opponentName;
	
	// Status
	public boolean dueling = false;
	boolean duelIsOver = false;
		
	public void setupFromPlayer(shandalike.data.character.Player player) {
		// Allow override of human deck from challenge file
		if(playerStart.deck == null)
			playerStart.deck = player.getInventory().getActiveDeck();
	}
	
	public void prepare() {
		setupFromPlayer(Model.adventure.getPlayer());
	}
	
	public void setupFromForgeQuestEvent(QuestEvent ev) {
		opponentName = ev.getTitle();
		// Load AI deck
		aiStart.deck = ev.getEventDeck();
		// Add extra cards
		for(String s: ev.getHumanExtraCards()) {
			// Delegate to the Forge questutil
			playerStart.permanents.add(QuestUtil.readExtraCard(s));
		}
		for(String s: ev.getAiExtraCards()) {
			aiStart.permanents.add(QuestUtil.readExtraCard(s));
		}
		// If the event is a "challenge" type it may have some extra info
		if(ev instanceof QuestEventChallenge) {
			QuestEventChallenge qc = (QuestEventChallenge)ev;
			aiStart.life = qc.getAiLife();
			playerStart.life = qc.getHumanLife();
			playerStart.deck = qc.getHumanDeck();
		}
		// Attach AI profile and name to AI lobby player
		aiLobbyPlayer = GamePlayerUtil.createAiPlayer(ev.getOpponent() == null ? ev.getTitle() : ev.getOpponent(), ev.getProfile());
	}
	
	/**
	 * Allows Shandalike to use Forge quest event decks. Loads from the given .dck file.
	 * @param ev
	 */
	public void loadForgeDuelFile(String duelFile) {
		// Open file; figure out if it is a QuestDuel or QuestChallenge...
		File f = Model.adventure.getWorld().getWorldFile("duel/" + duelFile);
		final Map<String, List<String>> contents = FileSection.parseSections(FileUtil.readFile(f));
		if(contents.get("quest") != null) {
			// This is a "challenge" file...
			this.setupFromForgeQuestEvent(QuestChallengeReader.readFromContents(contents, f));
		} else {
			// This is a "duel" file
			this.setupFromForgeQuestEvent(QuestDuelReader.readFromContents(contents));
		}
	}
	
	/**
	 * Start game according to preset parameters.
	 */
	public void startGame() {
		// NOTE: Copied this from QuestUtil.
		// Concoct player data
		human = new RegisteredPlayer(playerStart.deck);
		playerStart.applyToRegisteredPlayer(human);
		
		ai = new RegisteredPlayer(aiStart.deck);
		aiStart.applyToRegisteredPlayer(ai);
		
		// Setup game rules
		final GameRules rules = new GameRules(GameType.Shandalike);
		rules.setPlayForAnte(true);
		rules.setAnteDelegate(this);
		rules.setGamesPerMatch(1);
        rules.setManaBurn(FModel.getPreferences().getPrefBoolean(FPref.UI_MANABURN)); // honor forge prefs
        rules.setCanCloneUseTargetsImage(FModel.getPreferences().getPrefBoolean(FPref.UI_CLONE_MODE_SOURCE)); // honor forge prefs

        // Set up weird player stuff
        final List<RegisteredPlayer> registeredPlayers = new ArrayList<RegisteredPlayer>();
        humanLobbyPlayer = GamePlayerUtil.getQuestPlayer();
        registeredPlayers.add(human.setPlayer(humanLobbyPlayer)); // player.setPlayer()? wtf???????
        registeredPlayers.add(ai.setPlayer(aiLobbyPlayer));
        
        // Set up match
        final HostedMatch hostedMatch = GuiBase.getInterface().hostMatch();
        final IGuiGame gui = GuiBase.getInterface().getNewGuiGame();
        
        // Execute match
        result = new Result();
        dueling = true;
        FThreads.invokeInEdtNowOrLater(new Runnable(){
            @Override
            public void run() {
                hostedMatch.startMatch(rules, null, registeredPlayers, ImmutableMap.of(human, gui));
            }
        });
	}

	@Override
	public Multimap<Player, Card> getCardsForAnte(List<RegisteredPlayer> registeredPlayers, GameRules rules, Game game) {
		Multimap<Player, Card> antes = ArrayListMultimap.create();
		for(Player player: game.getPlayers()) {
			CardCollectionView library = player.getCardsIn(ZoneType.Library);
			if(player.getLobbyPlayer().equals(humanLobbyPlayer)) {
				AnteBuilder.matchPaperCardAnte(player, library, playerStart.ante, antes);
			} else if(player.getLobbyPlayer().equals(aiLobbyPlayer)) {
				AnteBuilder.matchPaperCardAnte(player, library, aiStart.ante, antes);
			}
		}
		
//		for(Player player: game.getPlayers()) {
//			CardCollectionView library = player.getCardsIn(ZoneType.Library);
//			Predicate<Card> antePredicate = Predicates.not(CardPredicates.Presets.BASIC_LANDS);
//			Card ante = Aggregates.random(Iterables.filter(library, antePredicate));
//			if(ante == null)
//				ante = Aggregates.random(library);
//			antes.put(player, ante);
//		}
		return antes;
	}
	
	public String getOpponentName() {
		if(opponentName == null) return "Unknown";
		return opponentName;
	}
	
	public boolean isOver() {
		return duelIsOver;
	}
	
	public boolean playerWon() {
		return result.playerDidWin;
	}

	// Callback invoked when anted cards change hands.
	public void onAnte(List<PaperCard> wonCards, List<PaperCard> lostCards) {
		if(wonCards != null) {
			Model.adventure.getPlayer().getInventory().addAllCards(wonCards);
			result.cardsLost.addAll(lostCards);
		}
		if(lostCards != null) {
			Model.adventure.getPlayer().getInventory().removeAllCards(lostCards);
			result.cardsWon.addAll(wonCards);
		}
	}
	
	public void onDuelEnded(boolean playerWonMatch) {
		duelIsOver = true;
		result.playerDidWin = playerWonMatch;
		menu.clear();
		Model.script.pcall(scriptDelegate, "duel_ended", new Object[]{this});
		Model.adventure.getPlayer().handleEvent("duelEnded", this, this.result);
		menu.update();
	}
	
	/**
	 * Determine if we can start a duel.
	 * @return
	 */
	public boolean canDuel() {
		// Can't already be a running duel
		if(Model.activeDuel != null) {
			return false;
		}
		// both players must have decks.
		if(playerStart.deck == null || aiStart.deck == null) {
			return false;
		}
		return true;
	}
	
	/**
	 * Perform duel setup and launch the duel screen.
	 */
	public void duel() {
		// Make sure player doesn't continue moving after duel
		Model.adventure.getWorldState().getActiveMapState().clearMovementMarker();
		Model.activeDuel = this;
		// Run preduel effects.
		Model.script.pcall(scriptDelegate, "duel_setup", new Object[]{this});
		Model.adventure.getPlayer().handleEvent("duelSetup", this, this.playerStart);
		if(aiEntity != null) {
			aiEntity.handleEvent("duelSetup", this, this.aiStart);
		}
		// Inform duel dialog to launch.
		Model.duelWillStart();
	}
	
	/**
	 * "Pass" duel -- nothing changes hands (e.g. bribe, run away, etc)
	 */
	public void pass() {
		cancel();
	}
	
	/**
	 * Cancel duel (if still at duel screen)
	 */
	public void cancel() {
		if((Model.activeDuel == null || Model.activeDuel == this) && !dueling) {
			Model.duelWillCancel();
		}
	}

	public void pushGameResult(GameResult gameResult, boolean matchIsOver) {
		if(gameResult.playerDidWin) {
			result.playerGamesWon++;
		} else {
			result.aiGamesWon++;
		}
		result.playerLifeTotal = Math.max(result.playerLifeTotal, gameResult.playerLifeTotal);
		result.aiLifeTotal = Math.max(result.aiLifeTotal, gameResult.aiLifeTotal);		
	}
}
