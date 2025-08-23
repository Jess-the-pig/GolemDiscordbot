package Golem.api.rpg.encounters.create_encounter;

import Golem.api.db.CampaignRepository;
import Golem.api.rpg.campaign.Campaign;
import Golem.api.rpg.campaign.CampaignNpc;
import Golem.api.rpg.characters.Characters;
import Golem.api.rpg.encounters.Encounters;
import Golem.api.rpg.encounters.create_encounter.choose_boss.DisplayMonstersAndBoss;
import Golem.api.rpg.encounters.create_encounter.fetch_monsters_by_terrain.MonsterByTerrain;
import Golem.api.rpg.encounters.create_encounter.roll_initiative.RollInitiative;
import Golem.api.rpg.encounters.create_encounter.set_difficulty.EncounterDifficulty;
import Golem.api.rpg.encounters.create_encounter.set_difficulty.EncounterEnemySize;
import Golem.api.rpg.encounters.create_encounter.set_difficulty.SetEncounterDifficulty;
import Golem.api.rpg.monsters.Monsters;
import com.austinv11.servicer.Service;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EncounterFactory {
  // TODO: mettre en place le biome
  private final MonsterByTerrain monsterByTerrain;
  // TODO:CHOISIR LES MONSTRES ET LA DIFFICULTe
  private final SetEncounterDifficulty setEncounterDifficulty;
  // TODO:Choisir le boss.
  private final DisplayMonstersAndBoss displayMonstersAndBoss;
  // Lien avec la campagne.
  private final CampaignRepository CampaignRepository;

  private final RollInitiative rollInitiative;

  public Encounters handle(
      EncounterDifficulty encounterDifficulty,
      String terrain,
      EncounterEnemySize encounterSize,
      ChatInputInteractionEvent event) {
    long channelId = event.getInteraction().getChannelId().asLong();

    Optional<Campaign> campaigntoEncounter = CampaignRepository.findByCampaignId(channelId);

    if (campaigntoEncounter.isEmpty()) {
      throw new NoSuchElementException("Campaign not found");
    }

    Encounters generatedEncounter = new Encounters();
    Campaign campaign = campaigntoEncounter.get();
    generatedEncounter.setCampaign(campaign);
    List<Characters> characters = campaign.getCharacters();
    generatedEncounter.setCharacters(characters);

    Integer avgLevel = findAvgLevelFromEncounterCharacters(characters, generatedEncounter);

    List<Monsters> encounteredMonsters =
        setEncounterDifficulty.getDifficultyDistribution(
            encounterDifficulty, terrain, encounterSize, avgLevel);

    List<CampaignNpc> encounterNpcs =
        campaigntoEncounter.map(Campaign::getCampaignNpcs).orElse(Collections.emptyList());

    generatedEncounter.setMonsters(encounteredMonsters);
    generatedEncounter.setInitiative(
        rollInitiative.initiativeSort(characters, encounteredMonsters, encounterNpcs));

    return generatedEncounter;
  }

  private Integer findAvgLevelFromEncounterCharacters(
      List<Characters> characters, Encounters generatedEncounter) {
    // Extraire les niveaux
    List<Integer> levels =
        characters.stream()
            .map(Characters::getLevel) // ton getter réel
            .toList();

    // Calculer la moyenne de niveau
    Integer avgLevel =
        (int) Math.round(levels.stream().mapToInt(Integer::intValue).average().orElse(1));

    return avgLevel;
  }
}
