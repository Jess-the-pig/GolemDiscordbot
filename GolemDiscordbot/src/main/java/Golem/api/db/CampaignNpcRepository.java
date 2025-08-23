package Golem.api.db;

import Golem.api.rpg.campaign.CampaignNpc;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository pour gérer les {@link CampaignNpc} dans la base de données. Fournit des opérations
 * CRUD via {@link JpaRepository} et des requêtes personnalisées.
 */
@Repository
public interface CampaignNpcRepository extends JpaRepository<CampaignNpc, Long> {
  /**
   * Récupère la liste des {@link CampaignNpc} associés à une campagne spécifique.
   *
   * @param campaign_id l'identifiant de la campagne
   * @return la liste des NPCs liés à la campagne
   */
  public List<CampaignNpc> findByCampaignId(Long campaign_id);
}
