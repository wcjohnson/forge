package shandalike.data.character;

import shandalike.Model;
import shandalike.data.entity.Pawn;
import shandalike.data.world.MapState;

public class Player extends Chara {
	public Inventory inventory;
	
	public Player() {
		super();
	}

	public Inventory getInventory() {
		// TODO Auto-generated method stub
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
