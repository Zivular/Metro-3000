package o1.adventure

class Obstacle(val name: String, val description: String, val itemNeededtoPass: String):
    
  override def toString = description
