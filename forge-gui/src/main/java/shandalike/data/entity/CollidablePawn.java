package shandalike.data.entity;

import com.badlogic.gdx.math.Rectangle;

import shandalike.Model;
import shandalike.data.effects.Effect;
import shandalike.data.entity.thought.ThinkState;
import shandalike.data.entity.thought.Thought;
import shandalike.game.CollisionState.Collision;

public class CollidablePawn extends Pawn {
	
	public transient boolean isColliding = false;
	public Effect collisionEffect;
	public String collisionScript;
	public Rectangle collisionRectangle = null;  // Override collision rectangle.
	
	public static class CollideWithPlayerThought implements Thought {
		@Override
		public void think(Entity entity, ThinkState thinkState) {
			CollidablePawn ep = (CollidablePawn) entity;
			Pawn pp = thinkState.mapState.getPlayerPawn();
			if(pp == null) return;
			Collision c = thinkState.collisionState.computeCollision(ep, pp);
			if(c != null) {
				// If already colliding, don't collide again.
				if(ep.isColliding) return;
				ep.isColliding = true;
				if(ep.collisionEffect != null)
					ep.collisionEffect.execute(ep, pp, thinkState.mapState, thinkState.worldState);
				if(ep.collisionScript != null)
					Model.script.pcall(ep.collisionScript, "collideWithPlayer", new Object[]{null, ep, pp, null});
				// Call through to behaviors
				ep.handleEvent("collideWithPlayer", pp, null);
			} else {
				ep.isColliding = false;
			}
		}	
	}
	
	
		
	@Override
	public void getCollisionRectangle(float pixelsPerUnit, Rectangle dest) {
		if(collisionRectangle != null) {
			dest.set(
					pos.x + collisionRectangle.x, pos.y + collisionRectangle.y,
					collisionRectangle.width, collisionRectangle.height
			);
		} else {
			super.getCollisionRectangle(pixelsPerUnit, dest);
		}
	}



	public CollidablePawn() {
		super();
		thoughts.add(new CollideWithPlayerThought());
	}
}
