package Golem.api.rpg.encounters.create_encounter.choose_boss;

import Golem.api.rpg.encounters.create_encounter.set_difficulty.ParseCr;
import Golem.api.rpg.monsters.Monsters;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DisplayMonstersAndBoss {
  private final ParseCr parseCr; // injecté par Spring

  public String displayMonstersAndBoss(List<Monsters> chosenMonsters) {
    if (chosenMonsters == null || chosenMonsters.isEmpty()) {
      return "No monsters selected.";
    }

    Monsters boss = null;
    double maxCr = -1;

    for (Monsters monster : chosenMonsters) {
      double cr = parseCr.parse(monster.getCr()); // ✅ appel à la méthode
      if (cr > maxCr) {
        maxCr = cr;
        boss = monster;
      }
    }

    StringBuilder sb = new StringBuilder();

    for (Monsters monster : chosenMonsters) {
      sb.append(monster.getName()).append(" (CR ").append(monster.getCr()).append(")");

      if (monster.equals(boss)) {
        sb.append(" => BOSS");
      }

      sb.append("\n");
    }

    return sb.toString();
  }
}
