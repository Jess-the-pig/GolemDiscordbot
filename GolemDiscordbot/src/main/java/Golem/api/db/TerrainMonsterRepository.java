package Golem.api.db;

import Golem.api.rpg.encounters.create_encounter.TerrainMonster;
import Golem.api.rpg.encounters.create_encounter.Terrains;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository pour gérer les {@link TerrainMonster} dans la base de données. Fournit des opérations
 * CRUD via {@link JpaRepository} et des requêtes personnalisées.
 */
@Repository
public interface TerrainMonsterRepository extends JpaRepository<TerrainMonster, Long> {
  /**
   * Récupère la liste des monstres associés à un terrain spécifique.
   *
   * @param terrain le terrain concerné
   * @return la liste des monstres présents sur le terrain
   */
  List<TerrainMonster> findByTerrain(Terrains terrain);
}
