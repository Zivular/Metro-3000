package o1.adventure

import scala.collection.mutable.Map
import scala.collection.mutable.Buffer
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
  private val craftingRecipes = Map[String, Vector[String]]("raft" -> Vector("wood", "nails"), "rope" -> Vector("thread"))
  private val craftableItems = Map[String, Item]("raft" -> Item("raft", "Looks flimsy, but it floats.", 0),
                                                   "rope" -> Item("rope", "Applicable in many situations, for example in climbing", 0))
  private val kuulaHealingPower = 50
  private val accuracy = 75
  private var attackPower = 10
  private var playerHealth = 100
  private var currentLocation = startingArea        // gatherer: changes in relation to the previous location
  private var quitCommandGiven = false              // one-way flag
  private val backPack = Map[String, Item]()     // container of all the items that the player has
  private val syödytVihreaKuulat = 0


  def backPackItem(itemName: String) = this.backPack(itemName)

  def switchWeapon(weaponName: String): String =
    if this.backPack.contains(weaponName) then
      this.attackPower = this.backPack(weaponName).damage
      s"You switched your weapon to $weaponName! Your attack power is now ${this.attackPower}!"
    else
      s"You don't have that weapon!"


  def currentHealth = if this.playerHealth >= 0 then this.playerHealth else 0

  def currentAttackPower = this.attackPower

  /** Determines if the player has indicated a desire to quit the game. */
  def hasQuit = this.quitCommandGiven

  /** Returns the player’s current location. */
  def location = this.currentLocation


  /** Attempts to move the player in the given direction. This is successful if there
    * is an exit from the player’s current location towards the direction name. Returns
    * a description of the result: "You go DIRECTION." or "You can't go DIRECTION." */
  def go(direction: String) =
    if this.location.returnObstacle.isEmpty || direction != "west" then
      val destination = this.location.neighbor(direction)
      this.currentLocation = destination.getOrElse(this.currentLocation)
      if destination.isDefined then "You go " + direction + "." else "You can't go " + direction + "."
    else
      s"You cannot go west, there is ${this.location.returnObstacle.head._1} blocking your way!"


  /** Causes the player to heal himself by eating some food.
    * Returns a description of what happened. */
  def eat(food: String): String =
    if this.backPack.contains(food) && food != "Crab meat" then {
      if this.playerHealth < 100 then {
        this.playerHealth += kuulaHealingPower
        this.backPack.remove(food)
        if this.playerHealth > 100 then
          this.playerHealth = 100
        s"You ate one kuula. You have never tasted anything this good before! Your health is now ${this.playerHealth}"
      } else s"Hyi saatana!🤮 You cannot eat any more kuula now"
    }

    else if this.backPack.contains(food) && food == "Crab meat" then {
      if this.playerHealth < 100 then {
        this.playerHealth += 60
        this.backPack.remove(food)
        if this.playerHealth > 100 then
          this.playerHealth = 100
        s"You are some crab meat, it tasted delicious! Your health is now {${this.playerHealth}"
      } else s"Smells too fishy! You cannot eat any more crab meat now"
    }
    else
      s"Unfortunately you have no kuula :("

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

  /** This methods takes care of the trading done with NPC characters */
  def trade(character: Character, item: Item): String = {
    if character.wantedItem == item.name then {
      val characterItem = character.currentOwnedItem.head._2
      this.backPack.put(characterItem.name, characterItem)
      character.currentOwnedItem.remove(characterItem.name)
      character.addItem(item)
      s"You traded a $item for ${characterItem.name}!"
    } else s"You don't have the required item for a trade"
  }

  /** Causes the player to examine the item of the given name. This is successful if such
    * an item is currently in the player’s possession. Returns a description of the result,
    * which, if the attempt is successful, includes a description of the item. The description
    * has the form: "You look closely at the ITEM.\nDESCRIPTION" or "If you want
    * to examine something, you need to pick it up first." */
  def examine(itemName: String) =
    def lookText(item: Item) = "You look closely at the " + item.name + ".\n" + item.description + "\n" + "Damage: " + item.damage
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
        s"You lack the necessary crafting items for $itemName!"
    else
      s"You have no idea how to craft $itemName!"

  def attack(certainHit: Boolean): String = {
    if Random.nextInt(101) <= this.accuracy || certainHit then
      this.location.returnMonster.head.takeDamage(this.attackPower)
      if this.location.returnMonster.head.currentHealth <= 0 then
        val monsterName = this.location.returnMonster.head.name
        this.location.killMonster(this.location.returnMonster.head)
        s"You killed the ${monsterName}!"
      else s"You strike the monster! It looses $attackPower health"
    else
      s"You miss!"
  }

  def showCraftableItems: String =
    s"Items you can craft, if you have sufficient materials:\n\n${this.craftableItems.keys.mkString(", ")}"

  def useItem(itemName: String): String = {
    if this.backPack.contains(itemName) then

      if itemName == "raft" && this.location.name == "Ruoholahti" then
        this.location.removeObstacle("water")
        s"You can now pass the water with the raft"

      else if itemName == "rope" && this.location.name == "lauttasaari" then
        this.location.removeObstacle("elevator shaft")
        s"You can now climb the elevator shaft to the second floor"

      else
        s"You have no use for $itemName here"

    else
      s"You don't have $itemName in your back pack"
  }

  def takeDamage(damageTaken: Int) =
    this.playerHealth -= damageTaken

  def counter =
    this.attack(true)

  def isDead: Boolean =
    this.playerHealth <= 0

end Player
