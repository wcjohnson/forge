package lib

import shandalike.Util
import shandalike.UIModel
import shandalike.Callback
import java.util.Collections
import forge.item.IPaperCard

class Quiz {
  String question
  int nChoices = 8
  def choices = []
  int correctAnswer
  Callback onCorrect
  Callback onIncorrect

  // Bury the right answer in some wrong answers.
  protected void bury(String rightAnswer, def wrongAnswers) {
    // Defensive-copy the wrong answers
    def wa = [*wrongAnswers]
    wa.add(rightAnswer)
    Collections.shuffle(wa)
    correctAnswer = wa.indexOf(rightAnswer)
    choices = wa
  }

  public void cardNumber(IPaperCard card, String numberDesc, int number) {
    question = "What is the ${numberDesc} of ${card.getRules().getName()}?"
    int shift = Util.randomInt(nChoices)
    int lb = Math.max(number - shift, 0)
    def wrongAnswers = []
    for(int i = lb; i < lb + nChoices; i++) {
      if(i != number) wrongAnswers.add("" + i)
    }
    bury("" + number, wrongAnswers)
  }

  protected void creaturePower(IPaperCard creature) {
    int power = creature.getRules().getIntPower()
    cardNumber(creature, "power", power)
  }

  public void generate() {
    // Generate a creature power quiz.
    // TODO: Add more kinds of quizzes.
    def creatures = Util.getFormat().getRandomCreatures(1)
    println "quiz: creatures ${creatures}"
    creaturePower(creatures[0])
  }

  public void show() {
    UIModel ui = new UIModel()
    ui.addPanel("Quiz", question)
    for(int i=0; i<choices.size(); i++) {
      ui.addButton(choices[i], this, "doChoose", i, null)
    }
    Util.pushUI(ui)
  }

  public void doChoose(int chosen, arg2) {
    Util.popUI()
    if(chosen == correctAnswer) {
      if(onCorrect != null) onCorrect.run()
    } else {
      if(onIncorrect != null) onIncorrect.run()
    }
  }

}
