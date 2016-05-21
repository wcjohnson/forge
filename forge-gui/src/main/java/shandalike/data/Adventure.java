package shandalike.data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import forge.properties.ForgeConstants;
import forge.util.FileUtil;
import shandalike.Constants;
import shandalike.Model;
import shandalike.Util;
import shandalike.data.character.Player;
import shandalike.data.entity.town.Town;
import shandalike.data.world.MapState;
import shandalike.data.world.WorldState;
import shandalike.game.GameTime;

public class Adventure {
    static {
        //ensure save directory exists if this class is used
        FileUtil.ensureDirectoryExists(Constants.USER_SHANDALIKE_SAVE_DIR);
    }
    
    // Summary data
    public AdventureSummary summary;
	// Player character state
	Player player;
	// List of world states
	Map<String, WorldState> worldStates;
	// Reference to the active world
	transient World world;
	
	public Adventure() {
		worldStates = new HashMap<String,WorldState>();
	}

	public String getName() {
		return summary.name;
	}
	
	public WorldState getWorldState() {
		return worldStates.get(world.id);
	}
	
	public World getWorld() { return world; }
	
	public void updateSummary() {
		summary.worldName = world.getName();
		summary.worldId = world.id;
		// Get gold and inventory stats
		if(player != null && player.getInventory() != null) {
			summary.gold = player.getInventory().getCurrency("gold");
			summary.nCards = player.getInventory().cardPool.countAll();
		}
		// Get player status
		WorldState ws = this.getWorldState();
		if(ws != null) {
			MapState ms = ws.getActiveMapState();
			if(ms != null) {
				if(ms.inTownId != null) {
					Town t = (Town)ms.getEntity(ms.inTownId);
					summary.status = "In town: " + t.name;
				} else {
					summary.status = "Wandering";
				}
			}
		}
	}

	/**
	 * Delete this adventure.
	 */
	public void delete() {
		File f = new File(Constants.USER_SHANDALIKE_SAVE_DIR, this.getName() + ".adventure");
		for(File c: f.listFiles()) c.delete();
		f.delete();
	}
	
	public String getAdventureBasePath() {
		return Constants.USER_SHANDALIKE_SAVE_DIR + this.getName() + ".adventure" + ForgeConstants.PATH_SEPARATOR;
	}
	
	// Save to the given slot
	public void save(int slot) {
		System.out.println(String.format("[Shandalike] Adventure %s: saving slot %d", getName(), slot));
		// Make directories
		String advPath = getAdventureBasePath();
		FileUtil.ensureDirectoryExists(advPath);
		
		// Make adventure summary
		updateSummary();
		summary.mostRecentSaveSlot = slot;
		// Dump summary
		String json = JSON.toJson(summary);
		FileUtil.writeFile(advPath + "summary.json", json);
		// Capture game time.
		this.getWorldState().gameTime = Util.getGameTime();
		// Dump char and world states
		json = JSON.toJson(this);
		FileUtil.writeFile(advPath + "slot" + slot + ".json", json);
	}
	
	// Load from the given slot.
	public boolean load(int slot) {
		if(this.summary == null) {
			throw new IllegalArgumentException("must load summary before loading slot");
		}
		String advPath = getAdventureBasePath();
		
		String json;
		json = FileUtil.readFileToString(advPath + "slot" + slot + ".json");
		if(json == null) return false;
		
		Adventure nextAdv = JSON.gson.fromJson(json, Adventure.class);
		this.summary = nextAdv.summary;
		this.player = nextAdv.player;
		this.worldStates = nextAdv.worldStates;
		world = Model.worlds.get(summary.worldId);
		world.hydrate();
		// Restore game time.
		GameTime gt = Model.gameController.gameTime;
		gt.sync(gt.wallTime(), getWorldState().gameTime);
		// Enter world
		world.willEnter();
		summary.mostRecentLoadSlot = slot;
		return true;
	}
	
	// Load summary information ONLY for the given adventure from its adventure base dir.
	public static Adventure loadSummary(File f) {
		String json = FileUtil.readFileToString(f.getPath() + ForgeConstants.PATH_SEPARATOR + "summary.json");
		AdventureSummary summary = JSON.gson.fromJson(json, AdventureSummary.class);
		
		Adventure adventure = new Adventure();
		adventure.summary = summary;
		return adventure;
	}
	
	public static Adventure newAdventure(AdventureSummary summary) {
		Adventure adventure = new Adventure();
		// Attach the adventure to the model as active so asset loaders work.
		Model.adventure = adventure;
		// Load up the world from the initial summary
		adventure.summary = summary;
		adventure.world = Model.worlds.get(summary.worldId); 
		adventure.updateSummary();
		// Create initial player and populate him into the world
		adventure.player = adventure.world.createInitialPlayerState(adventure);
		WorldState worldState = new WorldState(adventure.world);
		adventure.worldStates.put(adventure.world.id, worldState);
		adventure.world.createInitialWorldState(adventure, worldState);
		// Do an initial autosave in slot 0
		adventure.save(0);
		
		return adventure;
	}

	public Player getPlayer() {
		return player;
	}
}
