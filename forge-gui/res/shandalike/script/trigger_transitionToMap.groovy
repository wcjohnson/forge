import shandalike.data.entity.Entity
import shandalike.data.behavior.Behavior
import shandalike.Util

def methodMissing(String name, args) {
	null
}

// Zone the player to another map given by information on the behavior or map entities.
void collideWithPlayer(Behavior behavior, Entity trigger, Entity playerPawn, arg2) {
	String mapId = trigger.getVar("mapId")
	String baseMapId = trigger.getVar("baseMap")

	Util.getWorldState().transitionToMap(mapId, baseMapId)
}
