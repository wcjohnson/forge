import shandalike.Util
import shandalike.UIModel
import shandalike.Model
import lib.QuestController
import groovy.transform.Field

@Field def ui


void doBack(arg1, arg2) {
	Util.popUI()
}

void buildUI() {
	ui.addHeading("Objectives")
	QuestController qc = new QuestController()
	qc.buildOngoingUI(ui)
	ui.addHeading("Journal")
	ui.addButton("Go Back", this, "doBack", null, null)
}

void rebuildUI() {
	ui.clear()
	buildUI()
	ui.update()
}

void openScreen() {
	ui = new UIModel()
	buildUI()
	Util.pushUI(ui)
}
