package Golem.api.rpg.encounters.create_encounter.roll_initiative;

import Golem.api.common.interfaces.TimeStampedEntity;
import Golem.api.rpg.campaign.CampaignNpc;
import Golem.api.rpg.characters.Characters;
import Golem.api.rpg.dices.roll_dices.RollService;
import Golem.api.rpg.monsters.Monsters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RollInitiative {
  private RollService rollService;

  public List<Map<Long, TimeStampedEntity>> initiativeSort(
      List<Characters> characters, List<Monsters> monsters, List<CampaignNpc> campaignNpcs) {

    // Créer une map pour stocker tous les combattants et leur initiative
    Map<Long, TimeStampedEntity> initiativeMap = new HashMap<>();

    // Remplir la map avec Characters
    for (Characters c : characters) {
      Long initiative =
          rollService.rollDiceWithResult(20L); // méthode que tu dois avoir pour générer le score
      initiativeMap.put(initiative, c);
    }

    // Remplir la map avec Monsters
    for (Monsters m : monsters) {
      Long initiative = rollService.rollDiceWithResult(20L); // idem pour les monstres
      initiativeMap.put(initiative, m);
    }

    for (CampaignNpc n : campaignNpcs) {
      Long initiative = rollService.rollDiceWithResult(20L); // idem pour les monstres
      initiativeMap.put(initiative, n);
    }

    // Transformer en TreeMap triée du plus grand au plus petit
    TreeMap<Long, TimeStampedEntity> sortedMap =
        new TreeMap<>(java.util.Collections.reverseOrder());
    sortedMap.putAll(initiativeMap);

    // Retourner comme liste si nécessaire
    List<Map<Long, TimeStampedEntity>> result = new ArrayList<>();
    sortedMap.forEach((k, v) -> result.add(Map.of(k, v)));

    return result;
  }
}
