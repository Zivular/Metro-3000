package o1.adventure

/** The class `Action` represents actions that a player may take in a text adventure game.
  * `Action` objects are constructed on the basis of textual commands and are, in effect,
  * parsers for such commands. An action object is immutable after creation.
  * @param input  a textual in-game command such as “go east” or “rest” */
class Action(input: String):

  private val commandText = input.trim.toLowerCase
  private val verb       = commandText.takeWhile( _ != ' ' )
  private val modifiers  = commandText.drop(verb.length).trim

  /** Causes the given player to take the action represented by this object, assuming
    * that the command was understood. Returns a description of what happened as a result
    * of the action (such as “You go west.”). The description is returned in an `Option`
    * wrapper; if the command was not recognized, `None` is returned. */
  def execute(actor: Player) =

    if actor.location.returnMonster.isEmpty then
      this.verb match
      case "go"        => Some(actor.go(this.modifiers))

      case "eat"      => Some(actor.eat(this.modifiers))

      case "xyzzy"     => Some("The grue tastes yummy.")

      case "quit"      => Some(actor.quit())

      case "use" => Some(actor.useItem(this.modifiers))

      case "get"       => Some(actor.get(this.modifiers))

      case "drop"      => Some(actor.drop(this.modifiers))

      case "examine"   => Some(actor.examine(this.modifiers))

      case "inventory" => Some(actor.inventory)

      case "craft" => Some(actor.craftItem(this.modifiers))

      case "crafting" => Some(actor.showCraftableItems)

      case "attack" => Some("You have nothing to attack!")

      case "switch" => Some(actor.switchWeapon(this.modifiers))

      case other       => None
    else this.verb match

      case "eat" => Some(actor.eat(this.modifiers))

      case "attack" => Some(actor.attack(false))

      case "switch" => Some(actor.switchWeapon(this.modifiers))
      
      case "inventory" => Some(actor.inventory)

      case "quit" => Some(actor.quit())

      case other => None

  /** Returns a textual description of the action object, for debugging purposes. */
  override def toString = s"$verb (modifiers: $modifiers)"

end Action
