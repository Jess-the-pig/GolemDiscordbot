package Golem.api.db;

import Golem.api.rpg.campaign.Campaign;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository pour gérer les {@link Campaign} dans la base de données. Fournit des opérations CRUD
 * via {@link JpaRepository} et des requêtes personnalisées.
 */
@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
  /**
   * Récupère une campagne par son identifiant principal.
   *
   * @param id l'identifiant de la campagne
   * @return un {@link Optional} contenant la campagne si trouvée
   */
  public Optional<Campaign> findById(Long id);

  /**
   * Récupère une campagne par son identifiant spécifique de campagne.
   *
   * @param campaignId l'identifiant spécifique de la campagne
   * @return un {@link Optional} contenant la campagne si trouvée
   */
  public Optional<Campaign> findByCampaignId(Long campaignId);
}
