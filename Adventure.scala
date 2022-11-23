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

  private val itakeskus = Area("Station 1", "You are in Itäkeskus, it's dangerous in here.\nI need to to go west to get out of here")
  private val kalasatama = Area("Station 2", "You arrive at Kalasatama, there are a lot of dogs in here. Some of them seem angry")
  private val rautatientori = Area("Station 3", "You arrive at Rautatientori. There's a lot of people with guns, you need to be carefull not to be seen by them")
  private val kamppi = Area("Station 4", "You arrive at Kamppi, there are some hobos near. They look friendly")
  private val ruoholahti = Area("Station 5", "You arrive at Ruoholahti, there seems to be a lot of water at the exit. You need to make a raft to continue")
  private val lauttasaari = Area("Station 6", "You arrive at Lauttasaari, you hear music upstairs.")
  private val keilaniemi = Area("Station 7", "It's pitch black and you arrive at keilaniemi, it's eerie in here and you hear a loud yelling from the darkness infront of you.\nA furious and intoxicated konealfa attacks you!")
  private val otaniemi = Area("Station 8", "You see a bright light, you fall on the floor.")
  private val destination = otaniemi

  itakeskus.setNeighbors(Vector("west" -> kalasatama))
  kalasatama.setNeighbors(Vector("west" -> rautatientori))
  rautatientori.setNeighbors(Vector("west" -> kamppi))
  kamppi.setNeighbors(Vector("west" -> ruoholahti))
  lauttasaari.setNeighbors(Vector("west" -> keilaniemi))
  keilaniemi.setNeighbors(Vector("west" -> otaniemi))

  /** The character that the player controls in the game. */
  val player = Player(itakeskus)
  this.keilaniemi.addMonster(Monster("Konealfa", 500, 33, 66))


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
   else
      val monsterDamage = monster.attack
      if monsterDamage != 0 then println(s"The monster strikes you! You lose $monsterDamage health") else println(s"The monster misses!")
      player.takeDamage(monsterDamage)
  }

end Adventure
