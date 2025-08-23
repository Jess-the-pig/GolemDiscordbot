package Golem.api.rpg.encounters.create_encounter;

import Golem.api.common.entity.Combatant;

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
