import shandalike.Util

class Quiz {
  String question
  def choices
  int correctAnswer

  public void whichCreaturePower(creature, creatures) {
    int nBefore = Util.randomInt(4)
    int nAfter = Util.randomInt(4)
    correctAnswer = nBefore;
    for(int i=0; i<nBefore; i++) {

    }
  }

  public void creaturePower(creature, boolean invert) {
    // Pick a random creature
    randomCreature = creatures[Util.randomInt(creatures.size())]

  }
}
