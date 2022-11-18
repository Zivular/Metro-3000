package o1.adventure
import scala.collection.mutable.Map

class Monster(kind: String, baseHealth: Int, baseAttackPower: Int)  {

  def attack(attackPower: Double) = {

    }


  /* Määritellään erilaisia monstereita, kukin monsteri käyttää Monster luokan metodeja liittäen omat
  arvot metodien parametreihin*/

  // HellHoundilla on hyvä attackPower mutta heikko health
  object hellHound {
    private val attackP = attackPower * 1.5
    private val health = baseHealth * 0.75

    def currentHealth = this.health

    // Jos player jotain niin attack(attackP)

  }


}
