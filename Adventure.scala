package o1.adventure

import o1.adventure.ui.AdventureTextUI.player

/** The class `Adventure` represents text adventure games. An adventure consists of a player and
  * a number of areas that make up the game world. It provides methods for playing the game one
  * turn at a time and for checking the state of the game.
  *
  * N.B. This version of the class has a lot of “hard-coded” information that pertains to a very
  * specific adventure game that involves a small trip through a twisted forest. All newly created
  * instances of class `Adventure` are identical to each other. To create other kinds of adventure
  * games, you will need to modify or replace the source code of this class. */
class Adventure:

  /** the name of the game */
  val title = "Metro 3000"

  private val itakeskus = Area("Itkäkeskus", "You are in Itäkeskus, it's dangerous in here.\nI need to to go west to get out of here")
  private val kalasatama = Area("Kalasatama", "You arrive at Kalasatama, there are a lot of dogs in here. Some of them seem angry")
  private val rautatientori = Area("Rautatientori", "You arrive at Rautatientori. There's a lot of people with guns, you need to be carefull not to be seen by them")
  private val kamppi = Area("Kamppi", "You arrive at Kamppi, there are some hobos near. They look friendly")
  private val ruoholahti = Area("Ruoholahti", "You arrive at Ruoholahti, there seems to be a lot of water at the exit. You need to make a raft to continue")
  private val lauttasaari = Area("Lauttasaari", "You arrive at Lauttasaari, you hear music upstairs.")
  private val keilaniemi = Area("Keilaniemi", "It's pitch black and you arrive at keilaniemi, it's eerie in here and you hear a loud yelling from the darkness infront of you.\nA furious and intoxicated konealfa attacks you!")
  private val otaniemi = Area("Otaniemi", "You see a bright light, you fall on the floor.")
  private val abandonedMetroTrain = Area("Abandoned metro train", "This old train has been out of use for long now.")
  private val cafeteria = Area("Cafeteria", "Nice looking cafeteria")
  private val destination = otaniemi

  itakeskus.setNeighbors(Vector("west" -> kalasatama))
  kalasatama.setNeighbors(Vector("west" -> rautatientori, "east" -> abandonedMetroTrain))
  rautatientori.setNeighbors(Vector("west" -> kamppi, "east" -> cafeteria))
  kamppi.setNeighbors(Vector("west" -> ruoholahti))
  lauttasaari.setNeighbors(Vector("west" -> keilaniemi))
  keilaniemi.setNeighbors(Vector("west" -> otaniemi))
  ruoholahti.setNeighbors(Vector("west" -> lauttasaari))

  def areaName =
    this.player.location

  /** The character that the player controls in the game. */
  val player = Player(itakeskus)

  /** Obstacles and monster to be created to the game */
  this.keilaniemi.addMonster(Monster("Konealfa", 500, 33, 66))
  this.ruoholahti.addObstacle(Obstacle("water", "Deep and full of radioactive sharks", "raft"))
  this.lauttasaari.addObstacle(Obstacle("elevator shaft", "A black empty elevator shaft", "rope"))
  this.kalasatama.addMonster(Monster("radioactive dog", 30, 20, 60))

  /** Items the player can find from the areas */
  this.kalasatama.addItem(Item("knife", "Surprisingly it is still quite sharp, maybe you can use it to defend yourself", 30))
  this.rautatientori.addItem(Item("vihreä kuula", "Delicious looking green ball of perfection", 50))

  /** Determines if the adventure is complete, that is, if the player has won. */
  def isComplete = (this.player.location == this.destination)

  /** Determines whether the player has won, lost, or quit, thereby ending the game. */
  def isOver = this.isComplete || this.player.hasQuit || this.player.isDead


  /** Returns a message that is to be displayed to the player at the beginning of the game. */
  def welcomeMessage = s"The year is 3000. There has been a nuclear war on the surface of earth and people live in Metro tunnels.\nYou are located in Itäkeskus, Helsinki, Finland and you need to go to Otaniemi, Espoo to meet your scientist friends."


  /** Returns a message that is to be displayed to the player at the end of the game. The message
    * will be different depending on whether or not the player has completed their quest. */
  def goodbyeMessage =
    if this.isComplete then
      "\nzzzzzz\nzzzzzz\nzzzzzz\nWake up Mr. Freeman. Wake up and smell the ashes\nYou won!"
    else if this.player.isDead then
      "You died.\nGame over!"
    else  // game over due to player quitting
      "Quitter!"


  /** Plays a turn by executing the given in-game command, such as “go west”. Returns a textual
    * report of what happened, or an error message if the command was unknown. In the latter
    * case, no turns elapse. */
  def playTurn(command: String) =
    if player.location.returnMonster.nonEmpty then {
      fight(this.player.location.returnMonster.head, this.player.location)
    }
    val action = Action(command)
    val outcomeReport = action.execute(this.player)
    outcomeReport.getOrElse(s"Unknown command: \"$command\".")

  def fight(monster: Monster, area: Area) = {
   if monster.currentHealth <= 0 then
     println(s"You killed the ${monster.name}!")
     area.killMonster()
     this.player.location.fullDescription
   else
      val monsterDamage = monster.attack
      if monsterDamage != 0 then
        println(s"The ${monster.name} strikes you! You lose $monsterDamage health")
        player.takeDamage(monsterDamage)
      else println(s"You dodge the attack of ${monster.name} and you land a counter attack!")
        this.player.attack()

  }

end Adventure
