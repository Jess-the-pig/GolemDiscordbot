package Golem.api.repositories;

import Golem.api.entities.Characters;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharacterRepository extends JpaRepository<Characters, Long> {
  List<Characters> findByPlayerName(String playerName);

  Characters findByCharacterName(String characterName);

  List<Characters> findByCampaignId(Long campaign_id);
}
