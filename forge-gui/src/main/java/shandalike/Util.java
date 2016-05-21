package shandalike;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import forge.card.MagicColor;
import shandalike.data.World;
import shandalike.data.character.Inventory;
import shandalike.data.character.Player;
import shandalike.data.world.MapState;
import shandalike.data.world.WorldState;
import shandalike.mtg.Format;
import shandalike.mtg.ShopModel;

/**
 * Common utilities for scripting
 * @author wcj
 */
public class Util {
	/**
	 * Get the WorldState of the current world
	 * @return
	 */
	public static WorldState getWorldState() {
		return Model.adventure.getWorldState();
	}
	
	/**
	 * Get the MapState of the current map
	 */
	public static MapState getActiveMapState() {
		return Model.adventure.getWorldState().getActiveMapState();
	}
	
	/**
	 * @return The active Player.
	 */
	public static Player getPlayer() {
		return Model.adventure.getPlayer();
	}
	
	public static Inventory getPlayerInventory() {
		return getPlayer().getInventory();
	}
	
	/**
	 * Exit the current map, if possible.
	 */
	public static void exitMap() {
		getWorldState().exitMap();
	}
	
	public static void pushUI(UIModel model) {
		Model.gameEvent("pushView", model, null);
	}
	
	public static void popUI() {
		Model.gameEvent("popView", null, null);
	}
	
	public static void openShop(ShopModel shopModel) {
		Model.gameEvent("openShop", shopModel, null);
	}
	
	public static void openDecks() {
		Model.gameEvent("openDecks", null, null);
	}
	
	public static void showMessageBox(String msg, String title) {
		Model.gameEvent("showMessageBox", msg, title);
	}
	
	public static MagicColor.Color colorForName(String name) {
		for(MagicColor.Color c: MagicColor.Color.values()) {
			if(c.getName().equals(name)) return c;
		}
		return MagicColor.Color.COLORLESS;
	}
	
	public static float randomFloat() {
		return Model.rng.randomFloat();
	}
	
	public static int randomInt(int cap) {
		return Model.rng.randomInt(cap);
	}
	
	public static float getGameTime() {
		return Model.getGameTime();
	}
	
	public static boolean isCheatEnabled() {
		return Model.adventure.summary.isCheatEnabled;
	}
	
	public static World.DifficultySpec getDifficultySpec() {
		return Model.adventure.getWorld().getDifficultySpec();
	}
	
	/**
	 * General utility for picking items out of collections.
	 * See http://stackoverflow.com/questions/124671/picking-a-random-element-from-a-set
	 * @param coll
	 * @param index
	 * @return
	 */
	public static <T> T pick(Collection<? extends T> coll, int index) {
		if(coll.size() == 0) return null;
		if(coll instanceof List) {
			return ((List<? extends T>) coll).get(index);
		} else {
			Iterator<? extends T> iter = coll.iterator();
	        for (int i = 0; i < index; i++) {
	            iter.next();
	        }
	        return iter.next();
		}
	}
	
	private static Object storedContext = null;
	
	/**
	 * Pass a context to a script that will run later. Avoid using this if possible.
	 * @param obj
	 */
	public static void setContext(Object obj) {
		storedContext = obj;
	}
	
	/**
	 * Get the context set by setContext.
	 * @return
	 */
	public static Object getContext() {
		return storedContext;
	}
	
	/**
	 * Generate a sort of unique ID.
	 */
	public static String generateID() {
		return UUID.randomUUID().toString();
	}
	
	public static Format getFormat() {
		return Model.adventure.getWorld().getFormat();
	}
	
	public static void runScript(String scriptName, String method, Object arg) {
		Model.script.pcall(scriptName, method, arg);
	}
	
	public static void runScript(String scriptName, String method, Object... args) {
		Model.script.pcall(scriptName, method, args);
	}
}
