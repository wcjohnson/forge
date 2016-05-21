package shandalike.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.ImageResolver.AssetManagerImageResolver;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

import shandalike.data.JSON;

public class Sprite {
	public static class AnimationData {
		/** Should this animation loop? */
		boolean loop = true;
		/** Defines region of animation in texture */
		public int x;
		public int y;
		public int width;
		public int height;
		/** Defines how to split up the region into frames */
		public int tileWidth;
		public int tileHeight;
		/** Duration of each animation frame */
		public float frameDuration = 1.0f;
	}
	public static class JsonData {
		public String textureAsset;
		public final Vector2 size = new Vector2();
		public Vector2 origin;
		public Rectangle collisionRect;
		public final Map<String, AnimationData> animations = new HashMap<String, AnimationData>();
	}
	
	/** Sprite size */
	public final Vector2 dx = new Vector2();
	public final Vector2 origin = new Vector2();
	public final Rectangle collisionRect = new Rectangle();
	public TextureRegion texture;
	public Map<String, Animation> animations;
	
	public Sprite() {
		animations = new HashMap<String, Animation>();
	}
	
	/**
	 * Render this sprite
	 */
	public void render(RenderState renderState, float x, float y, String pose, float t, float scaleX, float scaleY, float rotation) {
		if(pose == null) return;
		Animation a = animations.get(pose);
		if(a == null) {
			// Use stand pose as default post
			a = animations.get("stand");
			if(a == null) {
				throw new GdxRuntimeException("sprite missing stand pose");
			}
		}
		// Get effective size of sprite.
		float edx = dx.x / renderState.pixelsPerUnit;
		float edy = dx.y / renderState.pixelsPerUnit;
		// Get effective origin
		Vector2 effOrigin = renderState.tempVec[0];
		effOrigin.set(origin).scl(1.0f / renderState.pixelsPerUnit);
		// If debug rendering is enabled, render collision rect underneath sprite.
		if(renderState.debugRendering) {
			Rectangle tempRect = renderState.tempRect[0];
			getCollisionRectangle(renderState.pixelsPerUnit, x, y, pose, tempRect);
			renderState.beginShapes();
			renderState.shapeRenderer.begin(ShapeType.Filled);
			renderState.shapeRenderer.setColor(1.0f, 0.5f, 0.5f, 0.75f);
			renderState.shapeRenderer.rect(
					tempRect.x, tempRect.y, 
					tempRect.width, tempRect.height
			);
			renderState.shapeRenderer.end();
			renderState.endShapes();
		}
		
		// Render the sprite
		TextureRegion frame = a.getKeyFrame(t);
		renderState.batch.draw(frame, x-(effOrigin.x), y-(effOrigin.y), -(effOrigin.x), -(effOrigin.y), edx, edy, scaleX, scaleY, rotation);
	}
	
	public void render(RenderState renderState, float x, float y, String pose, float t) {
		render(renderState, x, y, pose, t, 1.0f, 1.0f, 0.0f);
	}
	
	public void getCollisionRectangle(float ppu, float x, float y, String pose, Rectangle dest) {
		dest.set(
				x + (collisionRect.x / ppu),
				y + (collisionRect.y / ppu),
				collisionRect.width / ppu,
				collisionRect.height / ppu
		);
	}
		
	public static class Loader extends AsynchronousAssetLoader<Sprite, Loader.Parameters>{
		Sprite beingLoaded;
		
		protected static FileHandle getRelativeFileHandle (FileHandle file, String path) {
			StringTokenizer tokenizer = new StringTokenizer(path, "\\/");
			FileHandle result = file.parent();
			while (tokenizer.hasMoreElements()) {
				String token = tokenizer.nextToken();
				if (token.equals(".."))
					result = result.parent();
				else {
					result = result.child(token);
				}
			}
			return result;
		}
		
		public static class Parameters extends AssetLoaderParameters<Sprite> {
			/** generate mipmaps? **/
			public boolean generateMipMaps = false;
			/** The TextureFilter to use for minification **/
			public TextureFilter textureMinFilter = TextureFilter.Nearest;
			/** The TextureFilter to use for magnification **/
			public TextureFilter textureMagFilter = TextureFilter.Nearest;
		}
		
		public Loader() { super(new InternalFileHandleResolver()); }
		public Loader(FileHandleResolver resolver) { super(resolver); }
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Array<AssetDescriptor> getDependencies(String fileName, FileHandle spriteJson, Parameters parameter) {
			Array<AssetDescriptor> deps = new Array<AssetDescriptor>();
			try {
				JsonData jd = JSON.loadJson(spriteJson.file(), JsonData.class);
				boolean generateMipMaps = (parameter != null ? parameter.generateMipMaps : false);
				TextureLoader.TextureParameter texParams = new TextureParameter();
				texParams.genMipMaps = generateMipMaps;
				if (parameter != null) {
					texParams.minFilter = parameter.textureMinFilter;
					texParams.magFilter = parameter.textureMagFilter;
				}
				FileHandle handle = getRelativeFileHandle(spriteJson, jd.textureAsset);
				deps.add(new AssetDescriptor(handle, Texture.class, texParams));
				return deps;
			} catch(Exception e) {
				throw new GdxRuntimeException("Couldnt load textureAsset for sprite '" + fileName + "'", e);
			}
		}
		
		public void loadAsync(AssetManager manager, String fileName, FileHandle spriteJson, Parameters parameter) {
			beingLoaded = new Sprite();
			// Load json data
			JsonData jd = JSON.loadJson(spriteJson.file(), JsonData.class);
			// Load the sprite texture
			AssetManagerImageResolver resolver = new AssetManagerImageResolver(manager);
			FileHandle imagePath = getRelativeFileHandle(spriteJson, jd.textureAsset);
			beingLoaded.texture = resolver.getImage(imagePath.path());
			// Load size info
			beingLoaded.dx.set(jd.size);
			if(jd.origin != null) {
				beingLoaded.origin.set(jd.origin);
			} else {
				beingLoaded.origin.set(beingLoaded.dx.x / 2.0f, beingLoaded.dx.y / 2.0f);
			}
			if(jd.collisionRect != null) {
				beingLoaded.collisionRect.set(jd.collisionRect);
			} else {
				beingLoaded.collisionRect.set(-jd.size.x / 2.0f, -jd.size.y / 2.0f, jd.size.x, jd.size.y);
			}
			// Create the animations
			if( (jd.animations != null) && (jd.animations.size() > 0) ) {
				for(Entry<String, AnimationData> e: jd.animations.entrySet()) {
					AnimationData ad = e.getValue();
					TextureRegion tr = new TextureRegion(beingLoaded.texture, ad.x, ad.y, ad.width, ad.height);
					TextureRegion[][] subregions = tr.split(ad.tileWidth, ad.tileHeight);
					if(subregions.length > 0) {
						TextureRegion[] frames = new TextureRegion[subregions.length * subregions[0].length];
						int index = 0;
						for(int i=0; i<subregions.length; i++) {
							for(int j=0; j<subregions[i].length; j++) {
								frames[index++] = subregions[i][j];
							}
						}
						Animation animation = new Animation(ad.frameDuration, frames);
						if(ad.loop) animation.setPlayMode(Animation.PlayMode.LOOP);
						beingLoaded.animations.put(e.getKey(), animation);
					}				
				}
			} else {
				// No animation data; we'll just make a single Stand animation with the whole texture
				TextureRegion[] frame = new TextureRegion[1];
				frame[0] = beingLoaded.texture;
				Animation animation = new Animation(1.0f, frame);
				beingLoaded.animations.put("stand", animation);
			}
		}

		@Override
		public Sprite loadSync(AssetManager manager, String fileName, FileHandle file, Parameters parameter) {
			return beingLoaded;
		}
	}
	
}
