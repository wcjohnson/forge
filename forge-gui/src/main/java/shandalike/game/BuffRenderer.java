package shandalike.game;

import java.util.Collection;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Align;

import shandalike.Model;
import shandalike.data.behavior.Behavior;

public class BuffRenderer {
	public static final float buffHeight = 30.0f;
	public static final float buffWidth = 250.0f;
	public static final float buffGap = 5.0f;
	public static final float fontHeight = 15.0f;
	
	public void render(RenderState renderState, Collection<Behavior> collection) {
		float width = Gdx.graphics.getWidth(), height = Gdx.graphics.getHeight();
		float y = height;
		int effectsRendered = 0;
		renderState.tempMat4.setToOrtho2D(0, 0, width, height);
		renderState.batch.setProjectionMatrix(renderState.tempMat4);
		renderState.shapeRenderer.setProjectionMatrix(renderState.tempMat4);
		for(Behavior buff: collection) {
			if(effectsRendered > 20) break;
			if(!buff.isHidden() || Model.adventure.summary.isCheatEnabled) {
				renderBuff(renderState, buff, y);
				y -= (buffHeight + buffGap);
				effectsRendered++;
			}
		}
	}

	private void renderBuff(RenderState renderState, Behavior buff, float y) {
		// Draw backing rect
		renderState.shapeRenderer.begin(ShapeType.Filled);
		if(buff.isHelpful()) {
			renderState.shapeRenderer.setColor(0.0f, 0.6f, 0.3f, 0.8f);
		} else {
			renderState.shapeRenderer.setColor(0.6f, 0.0f, 0.0f, 0.8f);
		}
		renderState.shapeRenderer.rect(0, y-buffHeight, buffWidth, buffHeight);
		renderState.shapeRenderer.end();
		
		// Draw text
		renderState.batch.begin();
		renderState.bitmapFont.draw(renderState.batch, buff.getTitle(), 0.0f, y, buffWidth, Align.left, false);
		renderState.bitmapFont.draw(renderState.batch, buff.getDescription(), 0.0f, y - fontHeight, buffWidth, Align.left, false);
		renderState.batch.end();
	}
}
