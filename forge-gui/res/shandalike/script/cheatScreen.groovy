import shandalike.Util
import shandalike.UIModel
import shandalike.Model
import groovy.transform.Field

@Field def ui

void doFlushScripts(arg1, arg2) {
	println "[Shandalike] ----- FLUSHING SCRIPTS ------"
	Model.script.flushScripts()
}

void doToggleDebugRendering(arg1, arg2) {
	if(Model.gameController.renderState.debugRendering) {
		Model.gameController.renderState.debugRendering = false
	} else {
		Model.gameController.renderState.debugRendering = true
	}
}

void doBack(arg1, arg2) {
	Util.popUI()
}

void buildUI() {
	// Flush scripts
	ui.addButton("Flush scripts", this, "doFlushScripts", null, null)

	// Debug rendering
	ui.addButton("Toggle debug rendering", this, "doToggleDebugRendering", null, null)

	ui.addButton("Back", this, "doBack", null, null)
}

void openScreen() {
	if(!Model.adventure.summary.isCheatEnabled) return

	ui = new UIModel()
	buildUI()
	Util.pushUI(ui)
}