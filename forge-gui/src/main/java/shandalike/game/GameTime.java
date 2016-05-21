package shandalike.game;

public class GameTime {
	// Time of app launch
	private long startTime = 0;
	
	// Game time reference point
	private float game_t0 = 0.0f;
	// Wall time reference point. 
	private float wall_t0 = 0.0f;
	
	/**
	 * Is the clock paused?
	 */
	private boolean isPaused = true;
	
	/**
	 * Construct a new GameTime object. By default, the game time at the wall time of construction
	 * is set to 0.
	 */
	public GameTime() {
		startTime = System.currentTimeMillis();
		wall_t0 = wallTime();
	}
	
	/** Get the wall time (number of real seconds since Forge launch) in ms */
	public long wallTimeMs() { 
		return System.currentTimeMillis() - startTime; 
	}
	
	/** Get the wall time (number of real seconds since Forge launch) in floating-point seconds. ms precision. */
	public float wallTime() {
		return ((float)(System.currentTimeMillis() - startTime)) / 1000.0f;
	}
	
	/**
	 * Get the game time in floating-point seconds, with precision of ms.
	 * "Game time" is the amount of unpaused time spend in game.
	 * @return Game time in seconds.
	 */
	public float getGameTime() {
		if(isPaused)
			return game_t0;
		else
			return game_t0 + (wallTime() - wall_t0);
	}
	
	/**
	 * Set a new time reference.
	 */
	public void sync(float wallTime, float gameTime) {
		wall_t0 = wallTime; game_t0 = gameTime;
	}
	
	/**
	 * Resume a paused game clock.
	 */
	public void resume() {
		isPaused = false;
		wall_t0 = wallTime();
		System.out.println("[Shandalike] GameTime.resume(): resuming at " + getGameTime());
	}
	
	/**
	 * Pause the game clock.
	 */
	public void pause() {
		if(isPaused) return;
		game_t0 = getGameTime();
		isPaused = true;
		System.out.println("[Shandalike] GameTime.pause(): paused at " + getGameTime());
	}

	public boolean isPaused() {
		return isPaused;
	}
}
