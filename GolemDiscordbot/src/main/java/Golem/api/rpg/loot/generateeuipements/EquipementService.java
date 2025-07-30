package Golem.api.rpg.loot.generateeuipements;

import Golem.api.db.CharacterRepository;
import Golem.api.db.EquipementRepository;
import Golem.api.rpg.characters.Characters;
import Golem.api.rpg.dto.ReplyFactory;
import Golem.api.rpg.loot.generateloot.LootFactory;
import Golem.api.rpg.loot.generateloot.LootQuality;
import com.austinv11.servicer.Service;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class EquipementService {

  private final EquipementRepository equipementRepository;
  private final CharacterRepository characterRepository;
  private final LootFactory lootFactory;

  public Mono<Void> handleChestGeneration(
      ChatInputInteractionEvent event,
      int amountOfLoot,
      int amountOfChests,
      int level,
      Long campaignId) { // ✅ paramètre utilisé correctement

    List<Characters> campaignCharacters = characterRepository.findByCampaignId(campaignId);

    List<Integer> characterLevels =
        campaignCharacters.stream().map(Characters::getLevel).collect(Collectors.toList());

    int highestLevel = getHighestLevel(characterLevels);

    return ReplyFactory.reply(
        event, generateLoot(highestLevel, level, amountOfLoot, amountOfChests));
  }

  public String generateLoot(int playerLevel, int chestLevel, int amount, int numberOfChests) {
    LootQuality quality =
        getLootQuality(chestLevel, playerLevel); // ✅ inversé pour correspondre à ta logique
    List<Equipments> loot = equipementRepository.findAll();

    switch (quality) {
      case VERY_LOW:
        return lootFactory.pickRandomLoot(
            loot, amount, e -> e.getPrice_golds() < 20, numberOfChests);

      case LOW:
        return lootFactory.pickRandomLoot(
            loot, amount, e -> e.getPrice_golds() >= 20 && e.getPrice_golds() < 50, numberOfChests);

      case NORMAL:
        return lootFactory.pickRandomLoot(
            loot,
            amount,
            e -> e.getPrice_golds() >= 50 && e.getPrice_golds() < 100,
            numberOfChests);

      case HIGH:
        return lootFactory.pickRandomLoot(
            loot,
            amount,
            e -> e.getPrice_golds() >= 100 && e.getPrice_golds() < 200,
            numberOfChests);

      case EXCEPTIONAL:
        return lootFactory.pickRandomLoot(
            loot, amount, e -> e.getPrice_golds() >= 200, numberOfChests);

      default:
        return "Nothing";
    }
  }

  public LootQuality getLootQuality(int chestLevel, int playerLevel) {
    int delta = chestLevel - playerLevel;

    if (delta < -5) {
      return LootQuality.VERY_LOW;
    } else if (delta < 0) {
      return LootQuality.LOW;
    } else if (delta == 0) {
      return LootQuality.NORMAL;
    } else if (delta <= 5) {
      return LootQuality.HIGH;
    } else {
      return LootQuality.EXCEPTIONAL;
    }
  }

  public int getHighestLevel(List<Integer> levels) {
    return levels.stream()
        .mapToInt(Integer::intValue)
        .max()
        .orElseThrow(() -> new IllegalArgumentException("La liste ne peut pas être vide !"));
  }
}
