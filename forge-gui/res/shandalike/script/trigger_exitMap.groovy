// Generic trigger: exit the active map when collided with.
import shandalike.Util

def methodMissing(String name, args) { null }

void collideWithPlayer(behavior, trigger, playerPawn, arg2) {
	Util.exitMap()
}