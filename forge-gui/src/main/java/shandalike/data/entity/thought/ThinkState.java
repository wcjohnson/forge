package shandalike.data.entity.thought;


import com.badlogic.gdx.math.Vector2;

import shandalike.data.world.MapState;
import shandalike.data.world.WorldState;
import shandalike.game.CollisionState;

/**
 * Common values needed for thinking entities.
 * @author wcj
 */
public class ThinkState {
	public Vector2 tmpVec2[] = new Vector2[10];
	
	public ThinkState() {
		for(int i=0;i<10;i++) tmpVec2[i] = new Vector2();
	}

	/**
	 * Absolute system time
	 */
	public float t;
	
	/**
	 * Elapsed system time since last think cycle.
	 */
	public float dt;
	
	/**
	 * Reference to the mapstate.
	 */
	public MapState mapState;

	public CollisionState collisionState;

	public float gameTime;

	public WorldState worldState;
	
}
