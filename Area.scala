package o1.adventure

import scala.collection.mutable.Map

import scala.collection.mutable.Buffer
import scala.io.StdIn.*


/** The class `Area` represents locations in a text adventure game world. A game world
  * consists of areas. In general, an “area” can be pretty much anything: a room, a building,
  * an acre of forest, or something completely different. What different areas have in
  * common is that players can be located in them and that they can have exits leading to
  * other, neighboring areas. An area also has a name and a description.
  * @param name         the name of the area
  * @param description  a basic description of the area (typically not including information about items) */
class Area(var name: String, var description: String):

  private val neighbors = Map[String, Area]()
  private val contents = Map[String, Item]()
  private val obstacles = Map[String, Obstacle]()
  private var monster = Buffer[Monster]()
  private val characters = Buffer[Npc]()

  /** Returns the area that can be reached from this area by moving in the given direction. The result
    * is returned in an `Option`; `None` is returned if there is no exit in the given direction. */
  def neighbor(direction: String) = this.neighbors.get(direction)

  /** Adds an exit from this area to the given area. The neighboringf area is reached by moving in
    * the specified direction from this area. */
  def setNeighbor(direction: String, neighbor: Area): Unit =
    this.neighbors += direction -> neighbor

  /** Adds exits from this area to the given areas. Calling this method is equivalent to calling
    * the `setNeighbor` method on each of the given direction–area pairs.
    * @param exits  contains pairs consisting of a direction and the neighboring area in that direction
    * @see [[setNeighbor]] */
  def setNeighbors(exits: Vector[(String, Area)]): Unit =
    this.neighbors ++= exits


  /** Returns a multi-line description of the area as a player sees it. This includes a basic
    * description of the area as well as information about exits and items. If there are no
    * items present, the return value has the form "DESCRIPTION\n\nExits available:
    * DIRECTIONS SEPARATED BY SPACES". If there are one or more items present, the return
    * value has the form "DESCRIPTION\nYou see here: ITEMS SEPARATED BY SPACES\n\nExits available:
    * DIRECTIONS SEPARATED BY SPACES". The items and directions are listed in an arbitrary order. */
  def fullDescription =
    val exitList = "\n\nExits available: " + this.neighbors.keys.mkString(" ")
    val contentsList = if this.contents.isEmpty then "" else "\nYou see here: " + this.contents.keys.mkString(", ")
    if this.monster.nonEmpty then
      val actionlist = s"\nYou got into a fight with ${this.monster.head.name}\n\nFight commands:\nattack: Attacks the enemy with chosen weapon\neat [kuula name]: Eat kuula to regain health\nswitch [weapon name]: Switches weapon\ninventory: See your inventory\nquit: Quit the game\n"
      this.description + actionlist
    else if this.characters.nonEmpty then
      val tradeList = s" You see a ${this.characters.head.name}, they might have something valuable to trade\n${exitList}"
      this.description + tradeList
    else
      this.description + contentsList + exitList


  /** Returns a single-line description of the area for debugging purposes. */
  override def toString = this.name + ": " + this.description.replaceAll("\n", " ").take(150)


  /** Places an item in the area so that it can be, for instance, picked up. */
  def addItem(item: Item): Unit =
    this.contents.put(item.name, item)

  def addNpc(character: Npc): Unit = {
    this.characters += character
  }

  def addMonster(monster: Monster): Unit = {
    this.monster += monster
  }

  def killMonster(monster: Monster): Unit = {
    this.monster -= monster
  }

  def returnMonster: Buffer[Monster] = {
    monster
  }

  def returnNpc: Buffer[Npc] = {
    characters
  }

  def addObstacle(obstacle: Obstacle): Unit =
    this.obstacles.put(obstacle.name, obstacle)

  def removeObstacle(obstacle: String): Unit =
    this.obstacles.remove(obstacle)

  def returnObstacle: Map[String, Obstacle] =
    this.obstacles

  /** Determines if the area contains an item of the given name. */
  def contains(itemName: String) = this.contents.contains(itemName)



  /** Removes the item of the given name from the area, assuming an item with that name
    * was there to begin with. Returns the removed item wrapped in an `Option` or `None`
    * in the case there was no such item present. */
  def removeItem(itemName: String) =
    this.contents.remove(itemName)

end Area
