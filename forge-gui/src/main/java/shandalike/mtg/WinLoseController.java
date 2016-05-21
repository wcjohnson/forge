package shandalike.mtg;

import java.util.List;

import forge.LobbyPlayer;
import forge.game.GameOutcome;
import forge.game.GameView;
import forge.game.player.Player;
import forge.game.player.PlayerView;
import forge.interfaces.IButton;
import forge.interfaces.IWinLoseView;
import forge.item.PaperCard;
import forge.player.GamePlayerUtil;
import shandalike.Model;

/**
 * Controller responsible for processing win/loss results handed back from the Forge game engine.
 * Computes rewards, modifies player inventory, etc.
 * @author wcj
 */
public class WinLoseController {
	GameView game;
	IWinLoseView<? extends IButton> winLoseView;
	Duel duel;
	Duel.GameResult gameResult;
	boolean matchIsOver = false;
	boolean playerWonMatch = false;

	public WinLoseController(GameView gameView, IWinLoseView<? extends IButton> winLoseView) {
		this.game = gameView; this.winLoseView = winLoseView;
		duel = Model.activeDuel;
	}
	
	public void postGame() {
		System.out.println("[Shandalike] WinLoseController.postGame()");
		gameResult = new Duel.GameResult();
		// Don't allow restart of shandalike matches
		winLoseView.getBtnRestart().setVisible(false);
		
		// Disable quit buttons if match is not over yet
        matchIsOver = game.isMatchOver();
        if (matchIsOver) {
        	winLoseView.getBtnContinue().setVisible(false);
        	winLoseView.getBtnQuit().setText("Return to Shandalike");
        } else {
        	winLoseView.getBtnQuit().setVisible(false);
        }
		
		// Get internal forge data about players
        // XXX: The way Forge's internal player data structures work... escapes me.
        // LobbyPlayer versus PlayerView versus Player... none of them have the data I want.
        // I had to add new accessor methods just to get into the data structures that would let me see the game outcome
        // WHat gives????? Surely there is a better way.
        final LobbyPlayer questLobbyPlayer = GamePlayerUtil.getQuestPlayer();
        PlayerView player = null;
        for (final PlayerView p : game.getPlayers()) {
            if (p.isLobbyPlayer(questLobbyPlayer)) { // what
                player = p;
            }
        }
        final PlayerView questPlayer = player;
        playerWonMatch = game.isMatchWonBy(questLobbyPlayer);
        gameResult.playerDidWin = game.isWinner(questLobbyPlayer);
        for(Player p: game.getOutcome().getPlayers()) { // What
        	if(p.getId() == questPlayer.getId()) { // what
        		gameResult.playerLifeTotal = p.getLife();
        	} else {
        		gameResult.aiLifeTotal = p.getLife();
        	}
        }
        
        // Register a callback to be executed when post-game processing finishes
        winLoseView.showRewards(new Runnable() {
			@Override
			public void run() {
                // Won/lost cards should already be calculated (even in a draw)
                final GameOutcome.AnteResult anteResult = game.getAnteResultWithoutCrashing(questPlayer);
                if (anteResult != null) {
                	duel.onAnte(anteResult.wonCards, anteResult.lostCards);
                    anteReport(anteResult.wonCards, anteResult.lostCards);
                }
                
                duel.pushGameResult(gameResult, matchIsOver);
                if(!matchIsOver) return;
			}
        	
        });

	}
	
	// Generate display for ante report
    private void anteReport(final List<PaperCard> cardsWon, final List<PaperCard> cardsLost) {
        if (cardsWon != null && !cardsWon.isEmpty()) {
            winLoseView.showCards("Spoils! Cards won from ante", cardsWon);
        }
        if (cardsLost != null && !cardsLost.isEmpty()) {
            winLoseView.showCards("Looted! Cards lost to ante", cardsLost);
        }
    }
	
	public void postQuit() {
		System.out.println("[Shandalike] WinLoseController.postQuit()");
		// Clear active duel from Shandalike model
		Model.activeDuel = null;
		duel.dueling = false;
       	duel.onDuelEnded(playerWonMatch);
	}
}
