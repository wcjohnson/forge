package shandalike.game;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

public class InputManager implements InputProcessor, FocusListener {
	
	Controller controller;
	Map<Integer,Boolean> keysDown;
	
	public InputManager(Controller controller) {
		this.controller = controller;
		keysDown = new HashMap<Integer,Boolean>();
	}

	@Override
	public boolean keyDown(int keycode) {
		System.out.println("[Shandalike] keyDown: " + keycode);
		if(keysDown.get(keycode) == null) {
			keysDown.put(keycode, true);
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		System.out.println("[Shandalike] keyUp: " + keycode);
		if(keysDown.get(keycode) != null) {
			keysDown.remove(keycode);
		}
		return true;
	}
	
	public boolean isKeyPressed(int keycode) {
		return (keysDown.get(keycode) != null);
	}

	@Override
	public boolean keyTyped(char character) {
		if(character == 'm') {
			controller.toggleMapZoom();
		}
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Vector3 proj = controller.renderState.camera.unproject(new Vector3(screenX, screenY, 0.0f));
		controller.gameMap.click(proj.x, proj.y);
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void clearKeyMap() {
		System.out.println("[Shandalike] Clearing keymap");
		keysDown.clear();
	}

	///////////////////////////////////////////////
	// FocusListener - clear keys down when canvas loses focus.
	@Override
	public void focusGained(FocusEvent e) {
		System.out.println("[Shandalike] Canvas: focus gained");
	}

	@Override
	public void focusLost(FocusEvent e) {
		System.out.println("[Shandalike] Canvas: focus lost");
		clearKeyMap();
	}

}
