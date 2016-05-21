package shandalike.data.entity;

import java.util.ArrayList;

import com.badlogic.gdx.math.Rectangle;

import shandalike.Model;
import shandalike.data.character.Chara;
import shandalike.data.entity.thought.Thought;
import shandalike.game.RenderState;
import shandalike.game.Sprite;

/**
 * A Pawn is an Entity with an associated sprite and animations that can be rendered.
 * @author wcj
 */
public class Pawn extends Renderable {
	public String spriteAsset;
	
	/** The Sprite for this pawn. */
	transient Sprite sprite;
	/** This pawn's current animation pose */
	transient public String pose = "stand";
	/** The Character associated with this pawn, if any. */
	public Chara character;
	
	public Pawn() {
		super();
		thoughts = new ArrayList<Thought>();
	}
	
	@Override
	public void load() {
		super.load();
		if(spriteAsset != null) {
			sprite = Model.gameController.assetManager.getAsset(spriteAsset, Sprite.class);
		}
	}
	
	@Override
	public void render(RenderState renderState) {
		if(sprite == null) return;
		sprite.render(renderState, pos.x, pos.y, pose, renderState.t);
	}
	
	public void getCollisionRectangle(float pixelsPerUnit, Rectangle dest) {
		if(sprite == null) {
			dest.set(0,0,0,0); return;
		}
		sprite.getCollisionRectangle(pixelsPerUnit, pos.x, pos.y, pose, dest);
	}
	
	/** Get the Character associated with this Pawn. */
	public Chara getCharacter() {
		if(character != null) return character;
		if(this instanceof PlayerPawn) return Model.adventure.getPlayer();
		return null;
	}
	
	/** Get the Character associated with this Pawn, creating it if nonexistent. */
	public Chara getOrCreateCharacter() {
		Chara c = getCharacter();
		if(c != null) return c;
		character = new Chara();
		return character;
	}
}
