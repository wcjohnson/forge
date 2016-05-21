package shandalike.game;

import java.io.File;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import forge.properties.ForgeConstants;
import shandalike.Constants;
import shandalike.Model;

public class AssetManager extends com.badlogic.gdx.assets.AssetManager {
	
	/**
	 * Resolve assets by name in Shandalike dirs.
	 * @param path Asset name
	 * @return FileHandle pointing to asset, or null if not found.
	 */
	public static File resolveAsset(String path) {
		//System.out.println(String.format("[Shandalike] Asset resolver called for %s", path));
		// First look for asset in world/assets dir
		String lookPath = null;
		File f = null;
		if(Model.adventure != null && Model.adventure.getWorld() != null) {
			lookPath = Model.adventure.getWorld().getAssetDir() + path;
			f = new File(lookPath);
			if (f.isFile()) {
				System.out.println(String.format("[Shandalike] Resolved world asset at path %s", lookPath));
				return f;
			}
		}
		// Then look for assets in global assets dir.
		lookPath = Constants.GLOBAL_SHANDALIKE_DIR + ForgeConstants.PATH_SEPARATOR + "asset" + ForgeConstants.PATH_SEPARATOR + path;
		f = new File(lookPath);
		if (f.isFile()) {
			System.out.println(String.format("[Shandalike] Resolved global asset at path %s", lookPath));
			return f;
		}
		System.out.println(String.format("[Shandalike] Failed to resolve asset %s", path));
		return null;
	}
	
	/**
	 * Create a GDX AssetManager that resolves assets in Shandalike's game dirs.
	 */
	public AssetManager() {
		super(new FileHandleResolver() {
			public FileHandle resolve(String path) {
				File f = resolveAsset(path);
				if(f != null) return new FileHandle(f); else return null;
			}
		}, true);

		configureLoaders();
	}
	
	/**
	 * Retrieve an asset from the Shandalike assets bundle. Blocks until asset is loaded.
	 * @param name Asset name.
	 * @param clazz The type of asset. Must have a registered loader.
	 * @return The loaded asset, or null if not found.
	 */
	public <T> T getAsset(String name, Class<T> clazz) {
		if(!isLoaded(name, clazz)) {
			System.out.println(String.format("[Shandalike] Loading asset %s", name));
			load(name, clazz);
			finishLoading();
		}
		return get(name, clazz);
	}

	// Apply asset loaders for Shandalike asset types.
	private void configureLoaders() {
		this.setLoader(TiledMap.class, new TmxMapLoader(this.getFileHandleResolver()));
		this.setLoader(Sprite.class, new Sprite.Loader(this.getFileHandleResolver()));
	}
}
