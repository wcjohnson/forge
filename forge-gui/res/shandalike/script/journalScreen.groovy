import shandalike.Util
import shandalike.UIModel
import shandalike.Model
import groovy.transform.Field

@Field def ui


void doBack(arg1, arg2) {
	Util.popUI()
}

void doAbandon(objective, arg2) {
	def objectives = Util.getPlayer().getObjectives()
	objective.runScript("doAbandon", objectives, null, null)
	rebuildUI()
}

void buildObjectivesUI(UIModel uim, boolean canComplete, boolean canAbandon) {
	def objectives = Util.getPlayer().getObjectives()
	objectives.each {
		String title = (String)it.runScript("getTitle", objectives, null, null)
		String description = (String)it.runScript("getDescription", objectives, null, null)
		boolean is Abandon = (boolean)it.runScript("canAbandon", objectives, null, null)
		boolean isComplete = (boolean)it.runScript("isComplete", objectives, null, null)
		if(canComplete && isComplete) {
			uim.addPanel(title, description, this, ["Complete", "doComplete", it] as Object[])
		} else if(canAbandon && isAbandon) {
			uim.addPanel(title, description, this, ["Abandon", "doAbandon", it] as Object[])
		} else {
			uim.addPanel(title, description, this)
		}
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
