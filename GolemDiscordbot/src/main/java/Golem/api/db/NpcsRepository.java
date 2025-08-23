package Golem.api.db;

import Golem.api.rpg.npcs.Npcs;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository pour gérer les {@link Npcs} dans la base de données. Fournit des opérations CRUD via
 * {@link JpaRepository} et des requêtes personnalisées.
 */
@Repository
public interface NpcsRepository extends JpaRepository<Npcs, Long> {
  /**
   * Récupère un NPC par son nom.
   *
   * @param name le nom du NPC
   * @return le NPC correspondant
   */
  Npcs findByName(String name);

  /**
   * Récupère la liste des NPCs associés à un utilisateur spécifique.
   *
   * @param username le nom de l'utilisateur
   * @return la liste des NPCs appartenant à l'utilisateur
   */
  List<Npcs> findByUsername(String username);
}
