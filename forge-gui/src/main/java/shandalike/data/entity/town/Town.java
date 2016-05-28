package shandalike.data.entity.town;

import java.util.ArrayList;
import java.util.List;

import shandalike.Model;
import shandalike.data.behavior.Behavior;
import shandalike.data.behavior.Behaviors;
import shandalike.data.entity.PointOfInterest;
import shandalike.data.world.MapState;

public class Town extends PointOfInterest {
	/** Town name */
	public String name;
	/** Information about the town's Card Shop. */
	public CardShop cardShop;
	/** Information about available quests in this town. */
	public Behaviors quests = new Behaviors();
	
	public Town() {
		this.labelOnMap = true;
	}
	
	public String getLabel() {
		return name;
	}
	
	public String getName() {
		return name;
	}

	public void enter() {
		System.out.println("[Shandalike] Town.enter(): " + name);
		// Mark player as in town
		Model.adventure.getWorldState().getActiveMapState().inTownId = this.id;
		
		// Suppress collision
		Model.adventure.getWorldState().getActiveMapState().suppressPlayerCollision();
		
		Model.playerWillEnterTown(this);
	}
	
	public void leave() {
		System.out.println("[Shandalike] Town.leave(): " + name);
		MapState ms = Model.adventure.getWorldState().getActiveMapState();
		// Mark player as out of town.
		ms.inTownId = null;
		// Suppress player collision
		ms.suppressPlayerCollision();
		// On leaving a town, flush the new card list.
		Model.adventure.getPlayer().getInventory().resetNewList();
		// Clear the destination marker.
		ms.removeEntityById("_move_target");
		// Autosave.
		Model.adventure.save(0);
	}
}
