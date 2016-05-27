import shandalike.data.entity.Entity
import shandalike.data.behavior.Behavior
import shandalike.data.entity.town.Town

import lib.TownController

// Every Behavior script must provide methodMissing, which will be called when your script
// does not handle the specific event that was triggered. Generally you will do nothing here.
def methodMissing(String name, args) {
	return null;
}

// Specific game events will call the method with the same name as the event name.
// The first parameter for every event is the behavior, and the second is the entity
// implementing hte behavior. The remaining args are event specific. If you do not
// know the type of the arg, leave it typeless, as incorrectly typing it will cause
// your method not to be called by Groovy.
void collideWithPlayer(Behavior behavior, Town town, Entity playerPawn, arg2) {
	println "town.collideWithPlayer"
	def townController = new TownController(town)
	townController.enter()
}
