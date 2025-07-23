package Golem.api.factories;

import Golem.api.entities.Monsters;
import Golem.api.entities.TerrainMonster;
import Golem.api.enums.EncounterDifficulty;
import Golem.api.enums.EncounterEnemySize;
import Golem.api.repositories.TerrainMonsterRepository;
import Golem.api.repositories.TerrainRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BiomeDistributionFactory {

  private final TerrainRepository terrainRepository;
  private final TerrainMonsterRepository terrainMonsterRepository;

  public List<Monsters> getBiomeDistributions(String terrainChoisis) {
    Optional<String> terrain = terrainRepository.findByName(terrainChoisis);

    // Clé : nom du terrain, Valeur : distribution des biomes pour ce terrain

    List<Monsters> distribution = new ArrayList<>();
    List<TerrainMonster> relations = terrainMonsterRepository.findByTerrain(terrain);

    for (TerrainMonster relation : relations) {
      distribution.add(relation.getMonster());
    }

    return distribution;
  }

  public List<Monsters> getDifficultyDistribution(
      EncounterDifficulty encounterDifficulty,
      String terrainChosen,
      EncounterEnemySize encounterEnemySize,
      Integer characterAvgLevel) {

    List<Monsters> availableMonsters = getBiomeDistributions(terrainChosen);
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
        double candidateCr = parseCr(candidate.getCr());

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

  public String displayMonstersAndBoss(List<Monsters> chosenMonsters) {
    if (chosenMonsters == null || chosenMonsters.isEmpty()) {
      return "No monsters selected.";
    }

    Monsters boss = null;
    double maxCr = -1;

    for (Monsters monster : chosenMonsters) {
      double cr = parseCr(monster.getCr());
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

  private double parseCr(String cr) {
    if (cr.contains("/")) {
      String[] parts = cr.split("/");
      return Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
    } else {
      return Double.parseDouble(cr);
    }
  }
}
