package Golem.api.db;

import Golem.api.rpg.encounters.TerrainMonster;
import Golem.api.rpg.encounters.Terrains;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TerrainMonsterRepository extends JpaRepository<TerrainMonster, Long> {

  List<TerrainMonster> findByTerrain(Terrains terrain);
}
