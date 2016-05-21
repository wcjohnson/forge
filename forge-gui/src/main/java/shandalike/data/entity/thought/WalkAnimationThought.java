package shandalike.data.entity.thought;

import shandalike.data.entity.Entity;
import shandalike.data.entity.Pawn;

public class WalkAnimationThought implements Thought {
	@Override public void think(Entity p, ThinkState ts) {
		Pawn pp = (Pawn)p;
		// Change pose based on motion
		float speed = pp.velocity.len();
		if( speed > 0.01f ) {
			float angle = pp.velocity.angle();
			if(angle < 45.0f || angle >= 315.0f ) {
				pp.pose = "walk_right";
			} else if (angle >= 45.0f && angle < 135.0f) {
				pp.pose = "walk_up";
			} else if (angle >= 135.0f && angle < 225.0f) {
				pp.pose = "walk_left";
			} else if (angle >= 225.0f && angle < 315.0f) {
				pp.pose = "walk_down";
			} else {
				pp.pose = "walk";
			}
		} else {
			pp.pose = "stand";
		}
	}
}