import shandalike.Util
import shandalike.Model
import groovy.transform.Field

void timer(ent) {
	// Eat food
	int foodEaten = Model.adventure.summary.difficulty + 1;
	println("Shandalike: eating ${foodEaten} food")
	if(!Util.getPlayerInventory().takeCurrency("food", (long)foodEaten)) {
		Util.gameOver(false, true, "You ran out of food and starved to death. Remember to buy food!")
	}
}
