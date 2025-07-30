package Golem.api.rpg.loot.generateloot;

import Golem.api.rpg.loot.generateeuipements.Equipments;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LootFactory {
  public String pickRandomLoot(
      List<Equipments> loot, int amount, Predicate<Equipments> filter, int numberofChests) {

    StringBuilder result = new StringBuilder();

    for (int i = 0; i < numberofChests; i++) {
      Random random = new Random();

      List<Equipments> filteredLoot = loot.stream().filter(filter).collect(Collectors.toList());

      Collections.shuffle(filteredLoot);

      int randomLimit = random.nextInt(amount) + 1;
      List<Equipments> pickedLoot =
          filteredLoot.stream().limit(randomLimit).collect(Collectors.toList());

      result.append(pickedLoot.toString()).append("\n");
    }

    return result.toString();
  }
}
