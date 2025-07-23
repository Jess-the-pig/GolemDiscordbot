package Golem.api.repositories;

import Golem.api.entities.TerrainMonster;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TerrainMonsterRepository extends JpaRepository<TerrainMonster, Long> {

  List<TerrainMonster> findByTerrain(Optional<String> terrain);
}
