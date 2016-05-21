package shandalike.data.entity.thought;

import shandalike.data.entity.Entity;
import shandalike.data.entity.Pawn;

public class Motion implements Thought {
	@Override public void think(Entity p, ThinkState ts) {
		float dt = ts.dt;
		// Pathological dt's can happen when the game sticks at loading or while debugging.
		// If dt is slower than 1 FPS, don't move the pawn at all.
		if(dt > 1.0) return;
		Pawn pp = (Pawn)p;
		pp.pos.x += dt * 4 * pp.velocity.x;
		pp.pos.y += dt * 4 * pp.velocity.y;
	}
}