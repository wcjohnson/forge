import shandalike.Util
import shandalike.UIModel
import shandalike.Model
import groovy.transform.Field

@Field def ui

void doQuit(arg1, arg2) {
	Util.closeShandalike()
}

void doLoad(arg1, arg2) {
	Util.popUI()
	Model.gameEvent("openLoadScreen", null, null)
}

void buildUI() {
	String title = "Game Over!"
	if(Model.adventure.disposition.didWin) {
		title = "Victory!"
	} else if(Model.adventure.disposition.didLose) {
		title = "Defeat!"
	}
	ui.addPanel(title, Model.adventure.disposition.reason, this)
	// If not iron man, let user load a prior save
	if(!Model.adventure.summary.isIronMan) {
		ui.addButton("Load Saved Game", this, "doLoad", null, null)
	} else {
		ui.addPanel("Iron Man", "Your game ended in Iron Man mode, and all Iron Men know that reloading is for sissies. For men such as us, death is permanent. Good luck on your next adventure!", this)
	}
	ui.addButton("Quit", this, "doQuit", null, null)
}

void openScreen() {
	ui = new UIModel()
	buildUI()
	Util.pushUI(ui)
}
