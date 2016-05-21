package shandalike.screens;

import forge.Singletons;
import forge.game.GameView;
import forge.gui.framework.FScreen;
import forge.match.NextGameDecision;
import forge.screens.match.CMatchUI;
import forge.screens.match.ControlWinLose;
import forge.screens.match.ViewWinLose;
import shandalike.mtg.WinLoseController;

public class ShandalikeWinLose extends ControlWinLose {
	WinLoseController wlc;

	public ShandalikeWinLose(ViewWinLose v, GameView game0, CMatchUI matchUI) {
		super(v, game0, matchUI);
		wlc = new WinLoseController(game0, v);
	}

	@Override
	public void actionOnContinue() {
		// TODO Auto-generated method stub
		super.actionOnContinue();
	}

	@Override
	public void actionOnRestart() {
		// TODO Auto-generated method stub
		super.actionOnRestart();
	}

	@Override
	public void actionOnQuit() {
		wlc.postQuit();
		nextGameAction(NextGameDecision.QUIT);
		// Return to shandalike screen
		Singletons.getControl().setCurrentScreen(FScreen.SHANDALIKE);
	}

	@Override
	public boolean populateCustomPanel() {
		wlc.postGame();
		return true;
	}
	
	

}
