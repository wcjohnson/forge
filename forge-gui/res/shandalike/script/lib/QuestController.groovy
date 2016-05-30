package lib
import shandalike.data.behavior.Behavior
import shandalike.Util
import shandalike.UIModel

class QuestController {
  // Mode for this controller. "Offer" = quests will be offered to the user,
  // "ongoing" = list of available quests, allow abandonment
  String mode = "offer"
  // Behavioral from which quests will be pulled.
  def newQuestSource
  // When populating town quest data, how many quests should appear?
  int nQuests = 1
  // Menu for quests
  UIModel questMenu

  // Locate objective matching tag. Useful for quest objectives with "dual use"
  // scripts that double as both objective and tracking buffs
  static Behavior getObjectiveForTag(String tag) {
    println "getObjectiveForTag ${tag}"
    return Util.getPlayer().getObjectives().find { tag.equals(it.tag) }
  }

  static boolean canTurnIn(def objective) {
    return objective.runScript("canTurnIn", Util.getPlayer().getObjectives(), null, null)
  }

  static boolean canTurnInAny() {
    def x = Util.getPlayer().getObjectives().find { QuestController.canTurnIn(it) }
    return (x != null)
  }

  // Populate the questSource (presumably a town quest list) with random
  // quests until it is at nQuests
  void populate(def town) {
    for(int i=newQuestSource.size(); i<nQuests; i++) {
      def quest = Util.runScript("quests", "getRandomQuest", town)
      if(quest) {
        newQuestSource.add(quest)
      }
    }
  }

  // Build town quest menu
  void buildMenu(UIModel ui) {
    questMenu = ui
    // Show completed quests
    def playerObjectives = Util.getPlayer().getObjectives()
    playerObjectives.each {
      if(canTurnIn(it)) {
        it.runScript("buildUI", playerObjectives, ui, "complete")
      }
    }
    // Offer new quests
    if(newQuestSource) {
      newQuestSource.each {
        it.runScript("buildUI", playerObjectives, ui, "offer")
      }
    }
  }


}
