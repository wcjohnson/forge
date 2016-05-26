// An Objective requiring the player to travel to another Town, and will be marked
// complete when the player gets there.

import shandalike.Util
import shandalike.UIModel
import shandalike.Callback
import shandalike.data.behavior.Behavior
import shandalike.data.behavior.IBehavioral
import shandalike.data.reward.Reward
import shandalike.data.reward.CardReward
import lib.QuestController
import lib.RewardController
import groovy.transform.Field


// Every behavior must implement methodMissing.
def methodMissing(String name, args) { null }

void buildPanel(String header, String body, String button, String method, obj, ui) {
  def elt = ui.addPanel(header, body)
  if(button != null) {
    Callback cb = new Callback.ScriptObject()
    cb.target = this; cb.method = method
    cb.args = [obj, ui, elt] as Object[]
    elt.addButton(button, cb)
  }
}

void buildUI(Behavior obj, IBehavioral player, UIModel ui, String mode) {
  String dest = obj.getVar("destinationName")
  Map rdesc = obj.getVar("reward")
  println "rdesc ${rdesc}"
  Reward r = RewardController.fromDescriptor(rdesc)
  String rinfo = r.getDescription()
  String longInfo = "<html>Travel to ${dest} and deliver a message.<br><br><b>Reward:</b><br>\n${rinfo}</html>"
  if(mode.equals("offer")) {
    buildPanel("New Quest", longInfo, "Accept", "doAcceptQuest", obj, ui)
  } else if (mode.equals("ongoing")) {
    buildPanel("Travel", longInfo, "Abandon", "doAbandonQuest", obj, ui)
  } else if (mode.equals("complete")) {
    buildPanel("Quest Complete!", longInfo, "Complete", "doCompleteQuest", obj, ui)
  }
}

void doAcceptQuest(obj, ui, elt) {
  // Add objectives to player
  Util.getPlayer().getObjectives().addBehavior(obj)
  ui.remove(elt)
  ui.update()
}

void doAbandonQuest(obj, ui, elt) {
  // Remove objective from player
  Util.getPlayer().getObjectives().removeBehavior(obj)
  ui.remove(elt)
  ui.update()
}

void doCompleteQuest(obj, ui, elt) {
  // Remove objective from player
  Util.getPlayer().getObjectives().removeBehavior(obj)
  Map rdesc = obj.getVar("reward")
  Reward r = RewardController.fromDescriptor(rdesc)
  r.build(); r.choose(); r.award()
  ui.remove(elt)
  ui.update()
}

String getObjectiveTitle(behavior, behavioral, arg1, arg2) {
  String dest = behavior.getVar("destinationName")
  return "Travel to ${dest}!"
}

String getObjectiveDescription(behavior, behavioral, arg1, arg2) {
  String dest = behavior.getVar("destinationName")
  if(behavior.getVar("isComplete")) {
    "Quest complete! Claim your reward!"
  } else {
    "Travel to ${dest} and deliver a message."
  }
}

String getTitle(behavior) {
  "Hidden objective_travel buff"
}

String getDescription(behavior) {
  "Tracks travel objective"
}

boolean isHidden(behavior) {
  true
}

boolean isHelpful(behavior) {
  true
}

boolean canAbandon(behavior, behavioral, arg1, arg2) {
  true
}

boolean isComplete(behavior, behavioral, arg1, arg2) {
  if(behavior.getVar("isComplete")) {
    true
  } else {
    false
  }
}

// Upon removal, dispell the tracking debuff from the player
void behaviorWillRemove(behavior, behavioral, arg1, arg2) {
  println "objective_travel.behaviorWillRemove"
  if(behavioral == Util.getPlayer().getObjectives()) {
    // Remove tracking debuff which has the same tag as this debuff
    println "objective_travel.behaviorWillRemove inner"
    Behavior.purgeByTag(Util.getPlayer(), behavior.tag)
  }
}

// Upon addition, add a tracking buff to the player that will trigger
// when he reaches the destination
void behaviorDidAdd(behavior, behavioral, arg1, arg2) {
  println "objective_travel.behaviorAdd"
  // This script serves a "dual use" as both the objective and tracking buff.
  // Only run this routine when the objective is being added to the objectives.
  if(behavioral == Util.getPlayer().getObjectives()) {
    println "objective_travel.behaviorAdd inner"
    // Assign a tag for later reference
    behavior.tag = Util.generateID()
    // Assign a buff to the player with this script.
    Behavior playerBuff = new Behavior()
    playerBuff.script = "objective_travel"
    playerBuff.tag = behavior.tag
    Util.getPlayer().addBehavior(playerBuff)
  }
}

void playerDidEnterTown(behavior, behavioral, town, townMenu) {
  Behavior obj = QuestController.getObjectiveForTag(behavior.tag)
  String destId = obj.getVar("destinationId")
  if(town.id.equals(destId)) {
    obj.setVar("isComplete", true)
  }
}

void townWillBuildMenu(behavior, player, town, townMenu) {
  Behavior obj = QuestController.getObjectiveForTag(behavior.tag)
  String destId = obj.getVar("destinationId")
  if(town.id.equals(destId) && obj.getVar("isComplete")) {
    buildUI(obj, Util.getPlayer().getObjectives(), townMenu, "complete")
  }
}