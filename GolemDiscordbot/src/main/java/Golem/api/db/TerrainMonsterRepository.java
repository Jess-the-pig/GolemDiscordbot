package Golem.api.repositories;

import Golem.api.entities.TerrainMonster;
import Golem.api.entities.Terrains;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TerrainMonsterRepository extends JpaRepository<TerrainMonster, Long> {

  List<TerrainMonster> findByTerrain(Terrains terrain);
}
