package Golem.api.db;

import Golem.api.rpg.encounters.create_encounter.Terrains;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository pour gérer les {@link Terrains} dans la base de données. Fournit des opérations CRUD
 * via {@link JpaRepository} et des requêtes personnalisées.
 */
@Repository
public interface TerrainRepository extends JpaRepository<Terrains, Long> {
  /**
   * Récupère un terrain par son nom.
   *
   * @param name le nom du terrain
   * @return un {@link Optional} contenant le terrain si trouvé
   */
  Optional<Terrains> findByName(String name);
}
