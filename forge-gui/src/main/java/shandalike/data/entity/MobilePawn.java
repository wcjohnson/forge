package shandalike.data.entity;

import shandalike.Util;
import shandalike.data.entity.thought.Motion;
import shandalike.data.entity.thought.ThinkState;
import shandalike.data.entity.thought.Thought;
import shandalike.data.entity.thought.WalkAnimationThought;

/**
 * 
 * @author wcj
 *
 */
public class MobilePawn extends CollidablePawn {
	/** True if this pawn will move. */
	public boolean moving = true;
	/** Distance to the player when the pawn will lock on. */
	public float lockRadius = 5.0f;
	/** Chance the mob will follow the player even outside the lock radius */
	public float followChance = 0.25f;
	/** Chance the mob will stop in its tracks. */
	public float stopChance = 0.25f;
	/** Last time this entity changed its tracking */
	public transient float lastTrack = 0.0f;
	/** Tracking state */
	public transient String trackingState = "wander";
	
	// Insert delays between state changes
	private boolean shouldDelay(ThinkState thinkState) {
		if(thinkState.gameTime - this.lastTrack < 2.5f) {
			return true;
		} else {
			this.lastTrack = thinkState.gameTime + Util.randomFloat() * 5.0f;
			return false;
		}
	}
	
	// Do state changes
	private void doStateChange(ThinkState thinkState) {
		float dice = Util.randomFloat();
		switch(this.trackingState) {
		case "stand":
			if(dice < followChance) { 
				this.trackingState = "follow"; 
			} else {
				this.trackingState = "wander";
			}
			break;
		
		case "follow":
			if(dice < stopChance) {
				this.trackingState = "stand";
			} else if (dice < stopChance + followChance) {
				this.trackingState = "follow";
			} else {
				this.trackingState = "wander";
			}
			break;
			
		case "wander":
			if(dice < stopChance) {
				this.trackingState = "stand";
			} else if (dice < stopChance + followChance) {
				this.trackingState = "follow";
			} else {
				this.trackingState = "wander";
			}
			break;
			
		case "lock":
			if(dice < stopChance) {
				this.trackingState = "dontlock";
			} 
			break;

		case "dontlock":
			this.trackingState = "follow";
			break;
		}
	}
	
	static class ChasePlayerThought implements Thought {		
		@Override
		public void think(Entity entity, ThinkState thinkState) {
			MobilePawn mp = (MobilePawn)entity;
			if(!mp.moving) return;
			Pawn pp = thinkState.mapState.getPlayerPawn();
			if(pp == null) return;
			// Lock on player if close enough.
			if(pp.distanceFrom(mp) < mp.lockRadius && !mp.trackingState.equals("dontlock")) {
				mp.trackingState = "lock";
			}
			boolean shouldDelay = mp.shouldDelay(thinkState);
			if(!shouldDelay) mp.doStateChange(thinkState);
			switch(mp.trackingState) {
			case "lock":
				mp.velocity.set(pp.pos).sub(mp.pos).nor().scl(mp.getMoveSpeed());
				break;
				
			case "stand":
				mp.velocity.set(0,0);
				break;
				
			case "follow":
				if(!shouldDelay) mp.velocity.set(pp.pos).sub(mp.pos).nor().scl(mp.getMoveSpeed());
				break;
				
			case "wander":
			case "dontlock":
				if(!shouldDelay)
					mp.velocity.set(Util.randomFloat() + 0.01f, Util.randomFloat() + 0.01f).nor().scl(mp.getMoveSpeed());
				break;
			}
		}
	}
	
	public float getMoveSpeed() {
		return 0.9f;
	}
	
	public MobilePawn() {
		super();
		thoughts.add(new ChasePlayerThought());
		thoughts.add(new Motion());
		thoughts.add(new WalkAnimationThought());
	}
}
