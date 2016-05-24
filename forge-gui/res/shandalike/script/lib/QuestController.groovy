package lib
import shandalike.data.behavior.Behavior
import shandalike.Util

class QuestController {
  // Locate objective matching tag. Useful for quest objectives with "dual use"
  // scripts that double as both objective and tracking buffs
  static Behavior getObjectiveForTag(String tag) {
    println "getObjectiveForTag ${tag}"
    return Util.getPlayer().getObjectives().find { tag.equals(it.tag) }
  }
}
