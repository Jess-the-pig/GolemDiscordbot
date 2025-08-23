package Golem.api.rpg.encounters.create_encounter.fetch_monsters_by_terrain;

import Golem.api.db.TerrainMonsterRepository;
import Golem.api.db.TerrainRepository;
import Golem.api.rpg.encounters.create_encounter.TerrainMonster;
import Golem.api.rpg.encounters.create_encounter.Terrains;
import Golem.api.rpg.monsters.Monsters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MonsterByTerrain {
  private final TerrainRepository terrainRepository;
  private final TerrainMonsterRepository terrainMonsterRepository;

  public List<Monsters> getBiomeDistributions(String terrainChoisis) {
    Optional<Terrains> terrainOpt = terrainRepository.findByName(terrainChoisis);

    Terrains terrain = terrainOpt.orElseThrow(() -> new RuntimeException("Terrain not found"));

    List<Monsters> distribution = new ArrayList<>();
    List<TerrainMonster> relations = terrainMonsterRepository.findByTerrain(terrain);

    for (TerrainMonster relation : relations) {
      distribution.add(relation.getMonster());
    }

    return distribution;
  }
}
