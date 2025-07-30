package Golem.api.rpg.dices;

import java.util.List;

public class RollResultFormatter {

  public String format(RollResult result, boolean showTotal) {
    StringBuilder sb = new StringBuilder("Résultats : ");
    List<Long> rolls = result.getRolls();
    for (int i = 0; i < rolls.size(); i++) {
      sb.append(rolls.get(i));
      if (i < rolls.size() - 1) {
        sb.append(" , ");
      }
    }
    if (showTotal) {
      sb.append("\nTotal : ").append(result.getTotal());
    }
    return sb.toString();
  }
}
