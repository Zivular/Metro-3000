package o1.adventure

import scala.collection.mutable.Map
import scala.util.Random


/** A `Player` object represents a player character controlled by the real-life user
  * of the program.
  *
  * A player object’s state is mutable: the player’s location and possessions can change,
  * for instance.
  *
  * @param startingArea  the player’s initial location */
class Player(startingArea: Area):

    /** Craftable items and their crafting recipes in the game */
  private val craftingRecipes = Map[String, Vector[String]]("raft" -> Vector("wood", "nails"), "knife" -> Vector("steel"), "rope" -> Vector("thread"))
  private val craftableItems = Map[String, Item]("raft" -> Item("raft", "Looks flimsy, but it floats.", 0), "knife" -> Item("knife", "You can use it to cut things", 25),
                                                   "rope" -> Item("rope", "Applicalbe in many situations, for example in climbing", 0))
  private val accuracy = 66
  private var attackPower = 10
  private var playerHealth = 100
  private var currentLocation = startingArea        // gatherer: changes in relation to the previous location
  private var quitCommandGiven = false              // one-way flag
  private val backPack = Map[String, Item]()     // container of all the items that the player has



  def currenHealth = this.playerHealth

  def currentAttackPower = this.attackPower

  /** Determines if the player has indicated a desire to quit the game. */
  def hasQuit = this.quitCommandGiven

  /** Returns the player’s current location. */
  def location = this.currentLocation


  /** Attempts to move the player in the given direction. This is successful if there
    * is an exit from the player’s current location towards the direction name. Returns
    * a description of the result: "You go DIRECTION." or "You can't go DIRECTION." */
  def go(direction: String) =
    val destination = this.location.neighbor(direction)
    this.currentLocation = destination.getOrElse(this.currentLocation)
    if destination.isDefined then "You go " + direction + "." else "You can't go " + direction + "."


  /** Causes the player to rest for a short while (this has no substantial effect in game terms).
    * Returns a description of what happened. */
  def rest() =
    "You rest for a while. Better get a move on, though."


  /** Signals that the player wants to quit the game. Returns a description of what happened
    * within the game as a result (which is the empty string, in this case). */
  def quit() =
    this.quitCommandGiven = true
    ""


  /** Returns a brief description of the player’s state, for debugging purposes. */
  override def toString = "Now at: " + this.location.name


  /** Tries to pick up an item of the given name. This is successful if such an item is
    * located in the player’s current location. If so, the item is added to the player’s
    * inventory. Returns a description of the result: "You pick up the ITEM." or
    * "There is no ITEM here to pick up." */
  def get(itemName: String) =
    val received = this.location.removeItem(itemName)
    for newItem <- received do
      this.backPack.put(newItem.name, newItem)
    if received.isDefined then
      "You pick up the " + itemName + "."
    else
      "There is no " + itemName + " here to pick up."


  /** Determines whether the player is carrying an item of the given name. */
  def has(itemName: String) = this.backPack.contains(itemName)


  /** Tries to drop an item of the given name. This is successful if such an item is
    * currently in the player’s possession. If so, the item is removed from the
    * player’s inventory and placed in the area. Returns a description of the result
    * of the attempt: "You drop the ITEM." or "You don't have that!". */
  def drop(itemName: String) =
    val removed = this.backPack.remove(itemName)
    for oldItem <- removed do
      this.location.addItem(oldItem)
    if removed.isDefined then "You drop the " + itemName + "." else "You don't have that!"


  /** Causes the player to examine the item of the given name. This is successful if such
    * an item is currently in the player’s possession. Returns a description of the result,
    * which, if the attempt is successful, includes a description of the item. The description
    * has the form: "You look closely at the ITEM.\nDESCRIPTION" or "If you want
    * to examine something, you need to pick it up first." */
  def examine(itemName: String) =
    def lookText(item: Item) = "You look closely at the " + item.name + ".\n" + item.description
    val failText = "If you want to examine something, you need to pick it up first."
    this.backPack.get(itemName).map(lookText).getOrElse(failText)


  /** Causes the player to list what they are carrying. Returns a listing of the player’s
    * possessions or a statement indicating that the player is carrying nothing. The return
    * value has the form "You are carrying:\nITEMS ON SEPARATE LINES" or "You are empty-handed."
    * The items are listed in an arbitrary order. */
  def inventory =
    if this.backPack.isEmpty then
      "You are empty-handed."
    else
      "You are carrying:\n" + this.backPack.keys.mkString("\n")

 /** If itemName is defined as a craftable item, and player has sufficient items for crafting the item, this method adds this crafted itemName to backPack.
   * It also removes the items used in crafting, from the players backPack */
  def craftItem(itemName: String): String =
    if this.craftingRecipes.contains(itemName) then
      if this.craftingRecipes(itemName).forall(this.backPack.contains(_)) then
        this.backPack += (itemName -> this.craftableItems(itemName))
        this.craftingRecipes(itemName).foreach(this.backPack.remove(_))
        s"You successfully crafted $itemName!"
      else
        s"You lack the necessary craftingitems for $itemName!"
    else
      s"You have no idea how to craft $itemName!"

  def attack(): String = {
    if Random.nextInt(101) <= this.accuracy then
      this.location.returnMonster(0).takeDamage(this.attackPower)

      if monster.currentHealth <= 0 then
        area.killMonster()
        s"You killed the ${monster.name}!"
      else s"You strike the monster! It looses $playerDamage health"

    else
      s"You miss!"
  }

  def useItem(itemName: String) = {
  }

  def takeDamage(damageTaken: Int): Unit =
    this.playerHealth -= damageTaken

  def isDead: Boolean =
    this.playerHealth <= 0

end Player
