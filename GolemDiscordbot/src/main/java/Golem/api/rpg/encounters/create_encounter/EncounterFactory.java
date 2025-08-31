package Golem.api.rpg.encounters.create_encounter;

import Golem.api.common.interfaces.TimeStampedEntity;
import Golem.api.db.CampaignRepository;
import Golem.api.db.MonsterRepository;
import Golem.api.rpg.campaign.Campaign;
import Golem.api.rpg.campaign.CampaignNpc;
import Golem.api.rpg.characters.Characters;
import Golem.api.rpg.dto.InitiativeRoll;
import Golem.api.rpg.encounters.Encounters;
import Golem.api.rpg.encounters.Initiative;
import Golem.api.rpg.encounters.create_encounter.choose_boss.DisplayMonstersAndBoss;
import Golem.api.rpg.encounters.create_encounter.roll_initiative.RollInitiative;
import Golem.api.rpg.encounters.create_encounter.set_difficulty.EncounterDifficulty;
import Golem.api.rpg.encounters.create_encounter.set_difficulty.EncounterEnemySize;
import Golem.api.rpg.encounters.create_encounter.set_difficulty.SetEncounterDifficulty;
import Golem.api.rpg.monsters.Monsters;
import Golem.api.rpg.npcs.Npcs;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EncounterFactory {
    // TODO: mettre en place le biome
    private final MonsterRepository monsterRepository;
    // TODO:CHOISIR LES MONSTRES ET LA DIFFICULTe
    private final SetEncounterDifficulty setEncounterDifficulty;
    // TODO:Choisir le boss.
    private final DisplayMonstersAndBoss displayMonstersAndBoss;
    // Lien avec la campagne.
    private final CampaignRepository CampaignRepository;

    private final RollInitiative rollInitiative;

    @Transactional
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
                        encounterDifficulty, encounterSize, avgLevel);

        List<CampaignNpc> encounterNpcs =
                campaigntoEncounter.map(Campaign::getCampaignNpcs).orElse(Collections.emptyList());

        generatedEncounter.setMonsters(encounteredMonsters);

        List<InitiativeRoll> rolls =
                rollInitiative.initiativeSort(characters, encounteredMonsters, encounterNpcs);

        List<Initiative> initiatives = new ArrayList<>();
        int order = 0;
        for (InitiativeRoll roll : rolls) {
            Initiative initiative = new Initiative();
            initiative.setEncounter(generatedEncounter);
            initiative.setInitiativeOrder(order++);
            initiative.setInitiativeValue(roll.initiativeValue()); // on garde le score du jet

            TimeStampedEntity entity = roll.entity();
            if (entity instanceof Characters character) {
                initiative.setCharacter(character);
            } else if (entity instanceof Monsters monster) {
                initiative.setMonster(monster);
            } else if (entity instanceof Npcs npc) {
                initiative.setNpc(npc);
            }

            initiatives.add(initiative);
        }

        generatedEncounter.setInitiative(initiatives);

        return generatedEncounter;
    }

    @Transactional
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
