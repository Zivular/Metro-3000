package o1.adventure

class Obstacle(val description: String, val itemNeededtoPass: String):

  def isAbleToPass(usedItem: String): Boolean =
    usedItem == itemNeededtoPass
