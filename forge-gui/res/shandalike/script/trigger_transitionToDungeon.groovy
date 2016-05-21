import shandalike.data.entity.Entity
import shandalike.data.behavior.Behavior
import shandalike.Util
import shandalike.data.JSON

def methodMissing(String name, args) {
	null
}

// Zone the player to the dungeon given by the information on the trigger entity.
// Also applies debuffs accordingly
void collideWithPlayer(Behavior behavior, Entity trigger, Entity playerPawn, arg2) {
	String mapId = trigger.getVar("mapId")
	String baseMapId = trigger.getVar("baseMap")

	def debuffs = trigger.getVar("behaviors")
	if(debuffs) {
		// Deserialize and reserialize them as behaviors
		// Ugly but it works!
		debuffs.each {
			String s = JSON.toJson(it)
			Behavior beh = JSON.fromJson(s, Behavior.class)
			beh.tag = "dungeon" // Tag the behaviors so they can be dispelled when we leave dungeon
			playerPawn.addBehavior(beh)
		}
	}

	Util.getWorldState().transitionToMap(mapId, baseMapId)
}
