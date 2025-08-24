package Golem.api.db;

import Golem.api.rpg.characters.Characters;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository pour gérer les {@link Characters} dans la base de données. Fournit des opérations CRUD
 * via {@link JpaRepository} et des requêtes personnalisées.
 */
@Repository
public interface CharacterRepository extends JpaRepository<Characters, Long> {
  /**
   * Récupère la liste des personnages associés à un joueur spécifique.
   *
   * @param playerName le nom du joueur
   * @return la liste des personnages appartenant au joueur
   */
  List<Characters> findByuserId(Long userId);

  /**
   * Récupère un personnage par son nom.
   *
   * @param characterName le nom du personnage
   * @return le personnage correspondant
   */
  Characters findByCharacterName(String characterName);

  /**
   * Récupère la liste des personnages appartenant à une campagne spécifique.
   *
   * @param campaign_id l'identifiant de la campagne
   * @return la liste des personnages de la campagne
   */
  List<Characters> findByCampaignId(Long campaign_id);
}
