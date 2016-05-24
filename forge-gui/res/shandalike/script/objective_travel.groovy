import shandalike.Util
import shandalike.data.behavior.Behavior
// Assign player to travel to another town.

// Every behavior must implement methodMissing.
def methodMissing(String name, args) { null }

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

void doAbandon(behavior, behavioral, arg1, arg2) {
  behavioral.removeBehavior(behavior)
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
  println "objective_travel.behaviorAddr"
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
  String destId = behavior.getVar("destinationId")
  if(town.id.equals(destId)) {
    behavior.setVar("isComplete", true)
    townMenu.addButton("Quest Complete! Claim your reward!", this, "doClaimReward", behavior, null)
  }
}

void doClaimReward(behavior, arg2) {
  
}
