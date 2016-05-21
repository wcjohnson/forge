package shandalike.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import shandalike.data.entity.Pawn;

/**
 * Common state variables needed by renderer components.
 * @author wcj
 */
public class RenderState {
	public RenderState(Controller controller2) {
		this.controller = controller2;
		this.batch = new SpriteBatch();
		this.camera = new OrthographicCamera();
		this.bitmapFont = new BitmapFont();
		this.shapeRenderer = new ShapeRenderer();
		// urgh
		for(int i=0;i<10;i++) {
			this.tempVec[i] = new Vector2();
			this.tempRect[i]  = new Rectangle();
		}
	}

	/**
	 * Absolute system time
	 */
	public float t;
	
	/**
	 * Elapsed system time since last render.
	 */
	public float dt;
	
	/**
	 * The game time value
	 */
	public float gameTime = 0.0f;
	
	/**
	 * Current SpriteBatch being rendered.
	 */
	public SpriteBatch batch;
	
	/** Reference to the game controller. */
	public Controller controller;
	
	/**
	 * Reference to the active camera
	 */
	public OrthographicCamera camera;
	public float screenWidth = 1.0f;
	public float screenHeight = 1.0f;
	public float pixelsPerUnit = 1.0f;
	public float cameraX = 0.0f;
	public float cameraY = 0.0f;
	public float orthoWidth = 1.0f;
	public float orthoHeight = 1.0f;
	public boolean zoomOut = false;
	
	/**
	 * The player pawn entity.
	 */
	public Pawn playerPawn;
	
	// Temp objects to use while rendering
	public Vector2[] tempVec = new Vector2[10];
	public Vector3 tempVec3 = new Vector3();
	public Matrix4 tempMat4 = new Matrix4();
	public Rectangle[] tempRect = new Rectangle[10];
	
	/**
	 * Font for GDX text rendering
	 */
	public BitmapFont bitmapFont;
	
	/**
	 * Render debugging info
	 */
	public boolean debugRendering = false;
	
	public ShapeRenderer shapeRenderer;

	public GameTime gameTimeController;
	
	public void setupCamera(float screenWidth, float screenHeight, float pixelScale) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.pixelsPerUnit = pixelScale;
		this.orthoWidth = screenWidth/pixelScale;
		this.orthoHeight = screenHeight/pixelScale;
		resetCamera();
	}
	
	public void orthoToScreen() {
		this.orthoWidth = screenWidth/pixelsPerUnit;
		this.orthoHeight = screenHeight/pixelsPerUnit;
		resetCamera();
	}
	
	public void orthoFit(float w, float h) {
		Rectangle temp1 = tempRect[0], temp2 = tempRect[1];
		temp1.set(0,0,screenWidth/pixelsPerUnit,screenHeight/pixelsPerUnit);
		temp2.set(0,0,w,h);
		temp1.fitOutside(temp2);
		this.orthoWidth = temp1.width; this.orthoHeight = temp1.height;
		resetCamera();
	}
	
	public void resetCamera() {
		camera.setToOrtho(false, orthoWidth, orthoHeight);
		camera.position.x = cameraX; camera.position.y = cameraY;
		camera.update();
	}
	
	public void placeCamera(float x, float y) {
		cameraX = x; cameraY = y;
		camera.position.x = x; camera.position.y = y;
		camera.update();
	}
	
	public void endBatch() {
		batch.end();
	}
	public void beginBatch() {
		batch.begin();
	}
	public void resetCameraAndBeginBatch() {
		resetCamera();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
	}
	
	public void beginShapes() {
		batch.end();
		shapeRenderer.setProjectionMatrix(camera.combined);
	}
	
	public void endShapes() {
		resetCameraAndBeginBatch();
	}

}
