package shandalike;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import forge.interfaces.IProgressBar;
import shandalike.data.Adventure;
import shandalike.data.AdventureSummary;
import shandalike.data.ShandalikePreferences;
import shandalike.data.World;
import shandalike.data.character.Player;
import shandalike.data.ShandalikePreferences.Pref;
import shandalike.data.entity.town.Town;
import shandalike.game.AssetManager;
import shandalike.game.Controller;
import shandalike.game.RNG;
import shandalike.mtg.Duel;

public final class Model {
	// Forbid construction
	private Model() {}
	
	/** Data for all worlds */
	public static Map<String, World> worlds;
	/** Data for all adventures */
	public static Map<String, Adventure> adventures;
	/** Active adventures */
	public static Adventure adventure;
	/** Game asset manager. */
	public static AssetManager assetManager;
	/** Random generator */
	public static RNG rng;
	/** Currently active Duel */
	public static Duel activeDuel;
	/** EVent listeners */
	public static final List<IGameEventListener> listeners = new ArrayList<IGameEventListener>();
	/** Script engine */
	public static Scripting script;
 
	// Shandalike preferences
	public static ShandalikePreferences prefs;
	public static Controller gameController;
		
	public static void initialize(final IProgressBar progressBar) {
		rng = new RNG();
		script = new Scripting();
		assetManager = new AssetManager();
		prefs = new ShandalikePreferences();
		adventures = new HashMap<String, Adventure>();
		worlds = new HashMap<String, World>();
		loadWorldSummaries();
		loadAdventureSummaries();
	}
	
	public static void setActiveAdventure(Adventure aw) {
		adventure = aw;
		if(adventure == null) return;
		// Autoload most recent save.
		adventure.load(adventure.summary.mostRecentSaveSlot);
		adventures.put(adventure.getName(), adventure);
		// Save adventure pref.
		prefs.setPref(Pref.CURRENT_WORLD, aw.getName());
		prefs.save();
	}

	// Load all adventures from disk. Does not hydrate internal states.
	public static void loadAdventureSummaries() {
		final File worldDir = new File(Constants.USER_SHANDALIKE_SAVE_DIR);
		File[] files = worldDir.listFiles(new FilenameFilter() {
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".adventure");
            }
		});
		
		// Load each world.
		adventures.clear();
		if (files != null) {
			for(final File f: files) {
				Adventure w = Adventure.loadSummary(f);
				adventures.put(w.getName(), w);
			}
		}
	}
	
	/** Get a list of all world summaries */
	public static void loadWorldSummaries() {
		final File worldDir = new File(Constants.GLOBAL_SHANDALIKE_WORLDS_DIR);
		File[] files = worldDir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});
		
		worlds.clear();
		if (files != null) {
			for (File f: files) { World w = World.loadSummary(f); worlds.put(w.id, w); }
		}
	}
	
	public static Player getPlayer() {
		return Model.adventure.getPlayer();
	}
	
	// Create a new adventure.
	public static void createAdventure(AdventureSummary summary) {
		Adventure.newAdventure(summary);
	}

	public static void closeAdventure() {
		if(adventure == null) return;
		Model.activeDuel = null;
		setActiveAdventure(null);
	}
	
	public static void duelWillStart() {
		for(IGameEventListener listener: listeners) listener.duelWillStart(Model.activeDuel);
	}
	
	public static void duelWillCancel() {
		for(IGameEventListener listener: listeners) listener.duelWillCancel(Model.activeDuel);
	}
	
	public static void playerWillEnterTown(Town town) {
		for(IGameEventListener listener: listeners)
			listener.gameEvent("playerWillEnterTown", town, null);
	}
	
	public static void playerDidChangeMap() {
		for(IGameEventListener listener: listeners) listener.playerDidChangeMap();
	}
	
	public static void gameEvent(String event, Object arg1, Object arg2) {
		for(IGameEventListener listener: listeners)
			listener.gameEvent(event, arg1, arg2);
	}
	
	public static float getGameTime() {
		return gameController.gameTime.getGameTime();
	}


}
