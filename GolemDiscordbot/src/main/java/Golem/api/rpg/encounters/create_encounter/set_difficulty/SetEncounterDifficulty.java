package Golem.api.rpg.encounters.create_encounter.set_difficulty;

import Golem.api.rpg.encounters.create_encounter.fetch_monsters_by_terrain.MonsterByTerrain;
import Golem.api.rpg.monsters.Monsters;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SetEncounterDifficulty {
  private final MonsterByTerrain monsterByTerrain;
  private final ParseCr parseCr;

  public List<Monsters> getDifficultyDistribution(
      EncounterDifficulty encounterDifficulty,
      String terrainChosen,
      EncounterEnemySize encounterEnemySize,
      Integer characterAvgLevel) {

    List<Monsters> availableMonsters = monsterByTerrain.getBiomeDistributions(terrainChosen);
    List<Monsters> chosenMonsters = new ArrayList<>();

    Integer enemySize = encounterEnemySize.getRandomSize();

    // Budget total de CR pour tout le groupe d'ennemis
    double totalCrBudget = characterAvgLevel * encounterDifficulty.getFactor();

    // Budget par monstre
    double targetCrPerMonster = totalCrBudget / enemySize;

    Random random = new Random();

    for (int i = 0; i < enemySize; i++) {
      Monsters picked = null;

      // Essayons plusieurs fois pour trouver un CR proche du budget par monstre
      for (int attempt = 0; attempt < 100; attempt++) {
        Monsters candidate = availableMonsters.get(random.nextInt(availableMonsters.size()));
        double candidateCr = parseCr.parse(candidate.getCr());

        // Accepter si CR est proche du CR cible
        if (Math.abs(candidateCr - targetCrPerMonster) < 0.5) {
          picked = candidate;
          break;
        }
      }

      // Si pas trouvé après plusieurs tentatives, prendre un au hasard
      if (picked == null) {
        picked = availableMonsters.get(random.nextInt(availableMonsters.size()));
      }

      chosenMonsters.add(picked);
    }

    return chosenMonsters;
  }
}
