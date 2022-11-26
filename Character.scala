package o1.adventure

import scala.collection.mutable.Map

class Character(val name: String, val health: Int, val item: Map[String, Item], val wantedItem: String):

  private val ownedItem = item

  def currentOwnedItem = this.ownedItem

  def addItem(item: Item) = this.ownedItem.put(item.name, item)


end Character
