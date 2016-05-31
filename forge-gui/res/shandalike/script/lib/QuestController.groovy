package lib
import shandalike.data.behavior.Behavior
import shandalike.Util
import shandalike.UIModel
import shandalike.Callback

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
    return Util.getPlayer().getObjectives().find { tag.equals(it.tag) }
  }

  static boolean canTurnIn(def objective) {
    return objective.runScript("canTurnIn", Util.getPlayer().getObjectives(), null, null)
  }

  boolean isOngoing(def objective) {
    return true
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

  void buildPanel(Behavior objective, UIModel ui, String header, String body, String button, String method) {
    def elt = ui.addPanel(header, body)
    if(button != null) {
      Callback cb = new Callback.ScriptObject()
      cb.target = this; cb.method = method
      cb.args = [objective, ui, elt] as Object[]
      elt.addButton(button, cb)
    }
  }

  // Build town quest menu
  void buildMenu(UIModel ui) {
    questMenu = ui
    buildCompletedUI(ui)
    buildOfferUI(ui)
  }

  // Build Journal screen menu
  void buildOngoingUI(UIModel ui) {
    def playerObjectives = Util.getPlayer().getObjectives()
    playerObjectives.each {
      if(isOngoing(it)) {
        String descr = it.runScript("getObjectiveDescription", playerObjectives, null, null)
        buildPanel(it, ui, "Quest", "<html>${descr}</html>", "Abandon", "doAbandonQuest")
      }
    }
  }

  void buildCompletedUI(UIModel ui) {
    // Show completed quests
    def playerObjectives = Util.getPlayer().getObjectives()
    playerObjectives.each {
      if(canTurnIn(it)) {
        String descr = it.runScript("getObjectiveDescription", playerObjectives, null, null)
        buildPanel(it, ui, "Quest Complete!", "<html>${descr}</html>", "Complete", "doCompleteQuest")
        //it.runScript("buildUI", playerObjectives, ui, "complete")
      }
    }
  }

  void buildOfferUI(UIModel ui) {
    // Offer new quests
    def playerObjectives = Util.getPlayer().getObjectives()
    if(newQuestSource) {
      newQuestSource.each {
        String descr = it.runScript("getObjectiveDescription", playerObjectives, null, null)
        buildPanel(it, ui, "New Quest", "<html>${descr}</html>", "Accept", "doAcceptQuest")
        //it.runScript("buildUI", playerObjectives, ui, "offer")
      }
    }
  }

  void doAcceptQuest(obj, UIModel ui, elt) {
    // Add quest to objectives
    Util.getPlayer().getObjectives().addBehavior(obj)
    // Remove quest from ui
    ui.remove(elt)
    ui.update()
    // Remove quest from available quests
    newQuestSource.removeBehavior(obj)
  }

  void doCompleteQuest(obj, UIModel ui, elt) {
    // Remove quest from objectives
    Util.getPlayer().getObjectives().removeBehavior(obj)
    // Give rewards
    def rewardDescs = obj.getVar("rewards")
    ui.addHeading("Quest Completed!")
    RewardController.grantAwardsFromDescriptors(rewardDescs, ui)
    // Remove quest entry from completed UI
    ui.remove(elt)
    ui.update()
  }

  void doAbandonQuest(obj, UIModel ui, elt) {
    // Remove quest from objectives
    Util.getPlayer().getObjectives().removeBehavior(obj)
    // Remove quest box from ui
    ui.remove(elt)
    ui.update()
  }


}
