import shandalike.Util
import shandalike.UIModel
import shandalike.Model
import groovy.transform.Field

@Field def ui


void doBack(arg1, arg2) {
	Util.popUI()
}

void buildObjectivesUI(UIModel uim, boolean canComplete, boolean canAbandon) {
	def objectives = Util.getPlayer().getObjectives()
	objectives.each {
		it.runScript("buildUI", objectives, uim, "ongoing")
	}
}

void buildUI() {
	ui.addHeading("Objectives")
	buildObjectivesUI(ui, false, true)
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
