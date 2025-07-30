package Golem.api.db;

import Golem.api.rpg.encounters.Terrains;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TerrainRepository extends JpaRepository<Terrains, Long> {
  Optional<Terrains> findByName(String name);
}
