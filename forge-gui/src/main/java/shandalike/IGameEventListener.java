package shandalike;

import shandalike.data.entity.town.Town;
import shandalike.mtg.Duel;

/**
 * Listener for game events
 * @author wcj
 */
public interface IGameEventListener {
	/**
	 * Invoked by the game when it wants to start a duel.
	 */
	public void duelWillStart(Duel duel);
	
	/**
	 * Invoked by the game engine when the player transitions to a new map screen.
	 */
	public void playerDidChangeMap();

	/**
	 * Invoked when a duel is cancelled before starting.
	 * @param duel
	 */
	public void duelWillCancel(Duel duel);
	
	/**
	 * Invoked when a game event is triggered.
	 * @param event Event identifier.
	 * @param arg1
	 * @param arg2
	 */
	public void gameEvent(String event, Object arg1, Object arg2);
}
