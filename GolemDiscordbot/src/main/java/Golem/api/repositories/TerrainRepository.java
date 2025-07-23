package Golem.api.repositories;

import Golem.api.entities.Terrains;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TerrainRepository extends JpaRepository<Terrains, Long> {
  Optional<String> findByName(String name);
}
