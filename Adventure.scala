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
  private val kalasatama = Area("Kalasatama", "You are in Kalasatama, there are a lot of dogs in here. Some of them seem angry")
  private val rautatientori = Area("Rautatientori", "You are in Rautatientori. There's a lot of people with guns, you need to be carefull not to be seen by them")
  private val kamppi = Area("Kamppi", "You are in Kamppi, there are some hobos near. They look friendly")
  private val ruoholahti = Area("Ruoholahti", "You are in Ruoholahti, there seems to be a lot of water at the exit. You need to make a raft to continue")
  private val lauttasaari = Area("Lauttasaari", "You are in Lauttasaari, you hear music upstairs.")
  private val keilaniemi = Area("Keilaniemi", "It's pitch black and you arrive at keilaniemi, it's eerie in here and you hear a loud yelling from the darkness infront of you.\nA furious and intoxicated konealfa attacks you!")
  private val otaniemi = Area("Otaniemi", "You see a bright light, you fall on the floor.")
  private val abandonedMetroTrain = Area("Abandoned metro train", "This old train has been out of use for long now.")
  private val cafeteria = Area("Cafeteria", "Nice looking cafeteria")
  private val storage = Area("Storage", "Dark and quite small")
  private val oldBusTerminal = Area("Old bus terminal", "It is silent and dark in here!")
  private val k1Floor = Area("K1 Floor", "Second floor of the metro station, there is frienldy looking trader nearby.")
  private val destination = otaniemi

  itakeskus.setNeighbors(Vector("west" -> kalasatama))
  kalasatama.setNeighbors(Vector("west" -> rautatientori,"east" -> itakeskus, "south" -> abandonedMetroTrain))
  rautatientori.setNeighbors(Vector("west" -> kamppi,"east" -> kalasatama, "south" -> cafeteria))
  kamppi.setNeighbors(Vector("west" -> ruoholahti, "east" -> rautatientori, "south" -> oldBusTerminal))
  lauttasaari.setNeighbors(Vector("west" -> keilaniemi, "east" -> kamppi, "up" -> k1Floor))
  keilaniemi.setNeighbors(Vector("west" -> otaniemi, "east" -> lauttasaari))
  ruoholahti.setNeighbors(Vector("west" -> lauttasaari, "east" -> keilaniemi))
  abandonedMetroTrain.setNeighbors(Vector("north" -> kalasatama))
  cafeteria.setNeighbors(Vector("north" -> rautatientori, "south" -> storage))
  storage.setNeighbors(Vector("north" -> cafeteria))
  oldBusTerminal.setNeighbors(Vector("north" -> kamppi))
  k1Floor.setNeighbors(Vector("down" -> lauttasaari))




  def areaName =
    this.player.location

  /** The character that the player controls in the game. */
  val player = Player(itakeskus)

  /** Obstacles to be created to the game */
  this.ruoholahti.addObstacle(Obstacle("water", "Deep and full of radioactive sharks", "raft"))
  this.lauttasaari.addObstacle(Obstacle("elevator shaft", "A black empty elevator shaft", "rope"))

  /** Mosnters to be spawned to the game */
  this.keilaniemi.addMonster(Monster("Konealfa", 500, 33, 66))
  this.kalasatama.addMonster(Monster("radioactive dog", 30, 20, 60))
  this.rautatientori.addMonster(Monster("Bandit", 75, 25, 60))
  this.oldBusTerminal.addMonster(Monster("Robot", 125, 20, 80))
  this.lauttasaari.addMonster(Monster("radioactive crab", 50, 15, 90))

  /** Characters to be added to the game */

  /** Items the player can find from the areas */
  this.abandonedMetroTrain.addItem(Item("knife", "Surprisingly it is still quite sharp, maybe you can use it to defend yourself", 30))
  this.rautatientori.addItem(Item("vihreä kuula", "Delicious looking green ball of perfection", 0))
  this.cafeteria.addItem(Item("keltainen kuula", "Delicious looking yellow ball of perfection", 0))
  this.storage.addItem(Item("nails", "Might be useful for crafting", 0))
  this.oldBusTerminal.addItem(Item("wood", "You can build stuff with it!", 0))
  this.oldBusTerminal.addItem(Item("punainen kuula", "Delicious looking red ball of perfection", 0))
  this.ruoholahti.addItem(Item("violetti kuula", "Delicious looking violet ball of perfection", 0))
  this.lauttasaari.addItem(Item("thread", "Usable in crafting", 0))


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
    val action = Action(command)
    val outcomeReport = action.execute(this.player)
    outcomeReport.getOrElse(s"Unknown command: \"$command\".")


  def fight(monster: Monster, area: Area) = {
    var monsterDamage = monster.attack
    var monsterName = monster.name
    if monsterDamage == 0 then
      player.counter
      println(s"You dodge the attack of ${monsterName}. You land a counter attack and deal ${player.currentAttackPower} damage!")
      if this.player.location.returnMonster.isEmpty then println(s"You killed the ${monsterName}!")
    else
      player.takeDamage(monsterDamage)
      println(s"The ${area.returnMonster.head.name} strikes you! You lose $monsterDamage health")
    println(s"\nYour current health is: ${player.currenHealth}")
    println(s"Your current attack power: ${player.currentAttackPower}")
  }

end Adventure
