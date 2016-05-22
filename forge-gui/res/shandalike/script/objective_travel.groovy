// Assign player to travel to another town.

String getTitle(behavior, behavioral, arg1, arg2) {
  String dest = behavior.getVar("destinationName")
  return "Travel to ${dest}"
}

String getDescription(behavior, behavioral, arg1, arg2) {
  String dest = behavior.getVar("destinationName")
  return "Travel to ${dest} and deliver a message."
}

boolean canAbandon(behavior, behavioral, arg1, arg2) {
  true
}

boolean isComplete(behavior, behavioral, arg1, arg2) {
  false
}

void doAbandon(behavior, behavioral, arg1, arg2) {
  behavioral.removeBehavior(behavior)
}

// Upon removal, dispell the tracking debuff from the player
void behaviorWillRemove(behavior, behavioral, arg1, arg2) {

}

// Upon addition, add a tracking buff to the player that will trigger
// when he reaches the destination
void behaviorDidAdd(behavior, behavioral, arg1, arg2) {

}
