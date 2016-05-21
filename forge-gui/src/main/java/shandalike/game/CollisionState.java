package shandalike.game;

import java.util.ArrayList;

import com.badlogic.gdx.math.Rectangle;

import shandalike.data.entity.Entity;
import shandalike.data.entity.Pawn;
import shandalike.data.world.MapState;

public class CollisionState {
	public static class Collision {
		public static enum Type {
			TERRAIN,
			ENTITY,
			NONE
		}
		public Type type = Type.NONE;
		public Entity collider = null;
		public Entity collidee = null;
		public final Rectangle collideeRect = new Rectangle();
		
		public void clear() {
			type = Type.NONE; collider = null; collidee = null;
		}
	}
	
	public Collision[] collisions;
	public int numCollisions = 0;
	public ArrayList<Rectangle> blocks;
	public MapState mapState;
	public float pixelsPerUnit;
	public final Rectangle tempRect = new Rectangle();
	public final Rectangle tempRect2 = new Rectangle();
	
	public CollisionState() {
		blocks = new ArrayList<Rectangle>();
		collisions = new Collision[8];
		for(int i=0; i<8; i++) collisions[i] = new Collision();
	}
	
	public void clearBlocks() {
		blocks.clear();
	}
	public void addBlock(Rectangle block) {
		blocks.add(block);
	}
	
	public void clearCollisions() {
		numCollisions = 0;
		for(int i=0; i<8; i++) collisions[i].clear();
	}
	public boolean addCollision(Collision.Type type, Entity collider, Entity collidee, Rectangle collideeRect) {
		if (numCollisions >= 8) return false;
		collisions[numCollisions].type = type;
		collisions[numCollisions].collider = collider;
		collisions[numCollisions].collidee = collidee;
		collisions[numCollisions].collideeRect.set(collideeRect);
		numCollisions++;
		return true;
	}
	public void computeCollisions(Pawn p) {
		p.getCollisionRectangle(this.pixelsPerUnit, tempRect);
		if( tempRect.height < 0.001 || tempRect.width < 0.001) return;
		clearCollisions();
		for(Rectangle blocker: blocks) {
			if (blocker.overlaps(tempRect)) {
				if(!addCollision(Collision.Type.TERRAIN, p, null, blocker)) return;
			}
		}
	}
	public Collision computeCollision(Pawn p, Pawn q) {
		clearCollisions();
		p.getCollisionRectangle(this.pixelsPerUnit, tempRect);
		if( tempRect.height < 0.001 || tempRect.width < 0.001) return null;
		q.getCollisionRectangle(this.pixelsPerUnit, tempRect2);
		if( tempRect2.height < 0.001 || tempRect2.width < 0.001) return null;
		if( tempRect.overlaps(tempRect2) ) {
			addCollision(Collision.Type.ENTITY, p, q, tempRect);
			return collisions[0];
		}
		return null;
	}
	public Collision getTerrainCollision() {
		for(int i=0; i<numCollisions; i++) {
			if(collisions[i].type == Collision.Type.TERRAIN) return collisions[i];
		}
		return null;
	}

}
