package shandalike.data.entity;

import com.badlogic.gdx.math.Vector2;

public class Positional extends Entity {
	/** Entity's position on its containing map. By convention, pos is measured at the center of the bounding rect. */
	public final Vector2 pos = new Vector2(0,0);
	/** Entity's collision radius */
	public float radius;
	/** Entity's local bounding box. By convention, pos is measured at the center of the bounding rect. */
	public final Vector2 extents = new Vector2(0,0);
	/** Entity's velocity */
	public transient final Vector2 velocity = new Vector2();
	
	/**
	 * Measure distance from this pawn to another.
	 * @param other
	 * @return
	 */
	public float distanceFrom(Positional other) {
		Vector2 vec = new Vector2();
		return vec.set(pos).sub(other.pos).len();
	}
	
	public float distanceFrom(float x, float y) {
		Vector2 vec = new Vector2(x, y);
		return vec.sub(pos).len();
	}
}
