package shandalike.data;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

import forge.properties.ForgeConstants;
import forge.util.FileUtil;
import shandalike.Constants;
import shandalike.Model;
import shandalike.data.JSON.Templates;
import shandalike.data.character.Inventory;
import shandalike.data.character.Player;
import shandalike.data.entity.Entity;
import shandalike.data.world.MapInfo;
import shandalike.data.world.WorldState;
import shandalike.mtg.Format;
import shandalike.mtg.RandomPool;

public class World {
	public static class DifficultySpec {
		public Map<String,Long> startingCurrency;
		public int startingAmulets;
		public RandomPool.Spec startingPool;
		public float buyRatio = 1.0f;
		public float sellRatio = 0.5f;
	}
	
	// World name.
	private String name;
	public String id;
	public String getName() {
		return name;
	}
	
	/** World summary information */
	public transient WorldSummary summary;
	
	/** World map info */
	private transient Map<String, MapInfo> mapInfo;
	public transient String worldMapId = "_world";
	
	/** MTG format info */
	public Format.Spec formatSpec;
	/** Per-difficulty info */
	public DifficultySpec[] difficulties;
	/** Cards only obtainable as rewards. */
	public String[] rewardOnly;
	
	/**
	 * Entity templates
	 */
	public transient Map<String,Entity> entityTemplates;
	
	/** MTG card format */
	private transient Format format;
	
	/** Has the full world been loaded? */
	transient boolean isHydrated;
	
	public World() {
		mapInfo = new HashMap<String, MapInfo>();
	}
		
	/** Load summary information given a world's root dir */
	public static World loadSummary(File f) {
		World world = new World();
		world.id = f.getName();

		String json = FileUtil.readFileToString(f.getPath() + ForgeConstants.PATH_SEPARATOR + "world.json");
		world.summary = JSON.gson.fromJson(json, WorldSummary.class);
		
		world.name = world.summary.name;

		return world;
	}
	
	public String getWorldDir() {
		return Constants.GLOBAL_SHANDALIKE_WORLDS_DIR + id + ForgeConstants.PATH_SEPARATOR;
	}
	public String getAssetDir() {
		return getWorldDir() + "asset" + ForgeConstants.PATH_SEPARATOR;
	}
	public String getMapsDir() {
		return getWorldDir() + "map" + ForgeConstants.PATH_SEPARATOR;
	}
	public String getGlobalsDir() {
		return Constants.GLOBAL_SHANDALIKE_DIR + ForgeConstants.PATH_SEPARATOR;
	}
	
	/** Fully hydrate a world that was loaded summarily */
	public void hydrate() {
		if(isHydrated) return;
		World nextWorld = JSON.loadJson(getWorldDir() + "world.json", World.class);
		worldMapId = nextWorld.worldMapId;
		
		// Load MTG format
		formatSpec = nextWorld.formatSpec;
		format = new Format(name, nextWorld.formatSpec);
		difficulties = nextWorld.difficulties;
		rewardOnly = nextWorld.rewardOnly;

		// Load maps
		File[] files = new File(getMapsDir()).listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});
		mapInfo.clear();
		if(files != null) {
			for(final File f: files) {
				MapInfo m = MapInfo.loadSummary(f);
				mapInfo.put(m.id, m);
			}
		}
		
		// Load entity templates
		Templates t = JSON.templatesFromJson(
			JSON.loadFile(getGlobalsDir() + "entityTemplates.json"),
			null
		);
		t = JSON.templatesFromJson(
			JSON.loadFile(getWorldDir() + "entityTemplates.json"),
			t
		);
		String json = JSON.toJson(t);
		entityTemplates = JSON.fromJson(
			json, 
			new TypeToken<Map<String,Entity>>(){}.getType()
		);
		isHydrated = true;
	}

	/** Create an initial player state for this world. */
	public Player createInitialPlayerState(Adventure adventure) {
		hydrate();
		
		// Create the player
		Player player = new Player();
		player.inventory = Inventory.createInitialInventory(this, adventure.summary.difficulty, adventure.summary.getPlayerColor());
		
		return player;
	}
	
	public void createInitialWorldState(Adventure adventure,WorldState ws) {
		hydrate();
		// transition to the world map
		ws.transitionToMap(worldMapId, worldMapId, true);
	}

	public MapInfo getMapInfo(String mapId) {
		hydrate();
		MapInfo mi = mapInfo.get(mapId);
		if(mi == null) {
			throw new RuntimeException("map not found:" + mapId);
		}
		return mi;
	}
	
	public Format getFormat() {
		hydrate(); return format;
	}
	
	public DifficultySpec getDifficultySpec() {
		hydrate();
		return difficulties[Model.adventure.summary.difficulty];
	}

	public File getWorldFile(String string) {
		return new File(getWorldDir() + string);
	}

	public String getScriptsDir() {
		return getWorldDir() + "script" + ForgeConstants.PATH_SEPARATOR;
	}

	public void willEnter() {
		Model.script.flushScripts();
	}
}
