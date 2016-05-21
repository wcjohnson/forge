package shandalike.data.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;

import shandalike.game.RenderState;

/**
 * A Point of Interest is a map location with a possible label, that can be entered by the player.
 * @author wcj
 */
public class PointOfInterest extends CollidablePawn {
	/**
	 * If non-null, must be within this distance to see the PoI at all.
	 */
	public Float limitVisibleRadius;
	/**
	 * If true, PoI will be drawn when map is zoomed out.
	 */
	public boolean showOnMinimap = false;
	/**
	 * If true, PoI will be drawn with a label when map is zoomed out.
	 */
	public boolean labelOnMinimap = false;
	/**
	 * If true, PoI will be drawn with a label when map is zoomed in.
	 */
	public boolean labelOnMap = false;
	/**
	 * Map label
	 */
	public String label;
	
	/**
	 * @return The label text. Override in subclasses for dynamic labels.
	 */
	public String getLabel() {
		return label;
	}
	
	public boolean shouldShowOnMinimap() {
		return showOnMinimap;
	}
	
	public boolean shouldLabelOnMap() {
		return labelOnMap;
	}
	
	public boolean shouldLabelOnMinimap() {
		return labelOnMinimap;
	}
	
	@Override
	public void render(RenderState renderState) {
		if(sprite == null) return;
		// Distance clipping
		if(limitVisibleRadius != null) {
			float r = limitVisibleRadius;
			Pawn p = renderState.playerPawn;
			Vector2 v = renderState.tempVec[0];
			// Don't render if distance to player pawn is greater than radius.
			if(v.set(pos).sub(p.pos).len() > r) return;
		}
		// Draw sprite
		sprite.render(renderState, pos.x, pos.y, pose, renderState.t);
		// Draw label
		if(getLabel() != null && shouldLabelOnMap()) {
			// Project poi label position to screen space
			Vector3 screenPoint = renderState.camera.project(new Vector3(pos.x, pos.y - 1.0f, 0));
			// Terminate old batch.
			renderState.batch.end();
			// Change projection to screen-ortho coordinates
			renderState.tempMat4.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			renderState.batch.setProjectionMatrix(renderState.tempMat4);
			// Draw font as new batch
			renderState.batch.begin();
			renderState.bitmapFont.draw(renderState.batch, getLabel(), screenPoint.x, screenPoint.y, 0.0f, Align.center, false);
			renderState.batch.end();
			// Restore old proj matrix.
			renderState.batch.setProjectionMatrix(renderState.camera.combined);
			// Start new batch
			renderState.batch.begin();
		}
	}
}
