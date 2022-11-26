package o1.adventure

import scala.collection.mutable.Map

class Npc(val name: String, var item: Item, val wantedItem: String):



  def currentItem = this.item

  def addItem(item: Item) =
    this.item = item


end Npc
