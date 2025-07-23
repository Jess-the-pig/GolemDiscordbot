package Golem.api.utils;

import Golem.api.entities.interfaces.Combatant;

public class InitiativeEntry {
  private Combatant combatant;
  private int initiative;

  public InitiativeEntry(Combatant combatant, int initiative) {
    this.combatant = combatant;
    this.initiative = initiative;
  }

  public Combatant getCombatant() {
    return combatant;
  }

  public int getInitiative() {
    return initiative;
  }
}
