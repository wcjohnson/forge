package shandalike.data.entity;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

import shandalike.Model;
import shandalike.data.entity.thought.Motion;
import shandalike.data.entity.thought.ThinkState;
import shandalike.data.entity.thought.Thought;
import shandalike.data.entity.thought.WalkAnimationThought;
import shandalike.game.CollisionState;

public class PlayerPawn extends Pawn {
	
	static class SnapKeyboardInput implements Thought {
		@Override
		public void think(Entity entity, ThinkState thinkState) {
			if(!thinkState.mapState.getMapInfo().snapToTiles) return;
			PlayerPawn playerPawn = (PlayerPawn)entity;
			// Determine current player tile
			int tilex = (int)Math.floor(playerPawn.pos.x);
			int tiley = (int)Math.floor(playerPawn.pos.y);
			// Determine destination tile.
			if(Model.gameController.inputManager.isKeyPressed(Keys.LEFT)) {
				tilex -= 1;
			} else if(Model.gameController.inputManager.isKeyPressed(Keys.RIGHT)) {
				tilex += 1;
			} else if(Model.gameController.inputManager.isKeyPressed(Keys.UP)) {
				tiley += 1;
			} else if(Model.gameController.inputManager.isKeyPressed(Keys.DOWN)) {
				tiley -= 1;
			} else {
				tilex = -1;
			}
			// Out if no movement requested.
			if(tilex == -1) return;
			// Determine collision
			TiledMapTileLayer layer = (TiledMapTileLayer) thinkState.mapState.getMapInfo().getCollisionLayer();
			if(layer != null) {
				TiledMapTileLayer.Cell cell = layer.getCell(tilex, tiley);
				if(cell != null) {
					// Collided. Set oob
					//System.out.println("[Shandalike] snapToTileMap: Aborting kbd movement because of collision");
					tilex = -1;
				}
			}
			// Place target if no collision
			if(tilex > -1) {
				thinkState.mapState.placeMovementMarker((float)tilex + 0.5f, (float)tiley + 0.5f, false);
				//System.out.println("[Shandalike] snapToTileMap: placing map marker at tile " + tilex + "," + tiley);
			} else {
				//thinkState.mapState.removeEntityById("_move_target");
				//playerPawn.velocity.x = 0; playerPawn.velocity.y = 0;
			}
		}
	}
	
	static class FreeKeyboardInput implements Thought {
		@Override public void think(Entity entity, ThinkState ts) {
			if(ts.mapState.getMapInfo().snapToTiles) return;
			PlayerPawn playerPawn = (PlayerPawn)entity;
			Vector2 tmp = ts.tmpVec2[0];
			
			if(Model.gameController.inputManager.isKeyPressed(Keys.LEFT)) {
				tmp.x = -1;
			} else if(Model.gameController.inputManager.isKeyPressed(Keys.RIGHT)) {
				tmp.x = 1;
			} else {
				tmp.x = 0;
			}
			if(Model.gameController.inputManager.isKeyPressed(Keys.UP)) {
				tmp.y = 1;
			} else if(Model.gameController.inputManager.isKeyPressed(Keys.DOWN)) {
				tmp.y = -1;
			} else {
				tmp.y = 0;
			}

			if(tmp.len2() > 0.0) {
				// User has made keyboard input; clear mouse motion target.
				ts.mapState.clearMovementMarker();
				playerPawn.velocity.set(tmp).nor();
			} else {
				// No kbd motion
				playerPawn.velocity.set(tmp);
			}

		}
	}
	
	static class MoveToTarget implements Thought {
		@Override
		public void think(Entity entity, ThinkState thinkState) {
			PlayerPawn playerPawn = (PlayerPawn)entity;
			Vector2 tmp = thinkState.tmpVec2[0];
			// Auto motion target
			Positional targ = (Positional)thinkState.mapState.getEntity("_move_target");
			// Early out if no auto motion.
			if(targ == null) {
				if(thinkState.mapState.getMapInfo().snapToTiles) {
					// Zero velocity if no automotion on a snap to tile map.
					playerPawn.velocity.set(0,0);
				}
				return;
			}
			// Set velocity toward move target.
			tmp.set(targ.pos).sub(playerPawn.pos);
			if (tmp.len2() < 0.075) {
				thinkState.mapState.clearMovementMarker();
				playerPawn.velocity.x = 0; playerPawn.velocity.y = 0;
				// Snap Player to tile if enabled
				if(thinkState.mapState.getMapInfo().snapToTiles) {
					playerPawn.pos.x = (float)Math.floor(targ.pos.x) + 0.5f;
					playerPawn.pos.y = (float)Math.floor(targ.pos.y) + 0.5f;
				}
				return;
			}
			playerPawn.velocity.set(tmp).nor();
		}
	}
	
	static class WallCollision implements Thought {
		final Vector2 tmp = new Vector2();
		@Override public void think(Entity p, ThinkState ts) {
			// No standard wallCollision on snapToTiles maps.
			if(ts.mapState.getMapInfo().snapToTiles) return;
			
			PlayerPawn pp = (PlayerPawn)p;
			
			ts.collisionState.computeCollisions(pp);
			CollisionState.Collision c = ts.collisionState.getTerrainCollision();
			if(c != null) {
				// Cancel mouse movement on terrain collision
				ts.mapState.clearMovementMarker();
				// Apply velocity that sends me away from the center of the collidee rect.
				c.collideeRect.getCenter(tmp);
				tmp.sub(pp.pos).scl(-1);
				pp.velocity.set(tmp);
			}
		}
	}
		
	public PlayerPawn() {
		super();
		this.spriteAsset = "player.sprite.json";
		thoughts.add(new SnapKeyboardInput());
		thoughts.add(new FreeKeyboardInput());
		thoughts.add(new MoveToTarget());
		thoughts.add(new WallCollision());
		thoughts.add(new Motion());
		thoughts.add(new WalkAnimationThought());
	}
}
