package shandalike.data.character;

import shandalike.Model;
import shandalike.data.behavior.Behaviors;
import shandalike.data.entity.Pawn;
import shandalike.data.world.MapState;

public class Player extends Chara {
	public Inventory inventory;
	/**
	 * Quest objectives
	 */
	public Behaviors objectives = new Behaviors();
	/**
	 * Journal entries (dungeon clues etc)
	 */
	public Behaviors journalEntries = new Behaviors();
	
	public Player() {
		super();
	}
	
	public Behaviors getObjectives() {
		return objectives;
	}
	
	public Behaviors getJournalEntries() {
		return journalEntries;
	}

	public Inventory getInventory() {
		return inventory;
	}
	
	@Override
	public Pawn getPawn() {
		Pawn p = null;
		MapState ms = Model.adventure.getWorldState().getActiveMapState();
		if(ms != null) p = ms.getPlayerPawn();
		return p;
	}
}
