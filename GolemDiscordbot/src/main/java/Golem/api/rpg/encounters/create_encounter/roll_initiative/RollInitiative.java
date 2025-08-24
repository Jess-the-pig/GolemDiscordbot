package Golem.api.rpg.encounters.create_encounter.roll_initiative;

import Golem.api.rpg.campaign.CampaignNpc;
import Golem.api.rpg.characters.Characters;
import Golem.api.rpg.dices.roll_dices.RollService;
import Golem.api.rpg.dto.InitiativeRoll;
import Golem.api.rpg.monsters.Monsters;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RollInitiative {
  private final RollService rollService;

  public List<InitiativeRoll> initiativeSort(
      List<Characters> characters, List<Monsters> monsters, List<CampaignNpc> campaignNpcs) {

    List<InitiativeRoll> rolls = new ArrayList<>();

    // Characters
    for (Characters c : characters) {
      Long initiative = rollService.rollDiceWithResult(20L);
      rolls.add(new InitiativeRoll(initiative, c));
    }

    // Monsters
    for (Monsters m : monsters) {
      Long initiative = rollService.rollDiceWithResult(20L);
      rolls.add(new InitiativeRoll(initiative, m));
    }

    // NPCs
    for (CampaignNpc n : campaignNpcs) {
      Long initiative = rollService.rollDiceWithResult(20L);
      rolls.add(new InitiativeRoll(initiative, n));
    }

    // Tri du plus grand au plus petit
    rolls.sort((a, b) -> Long.compare(b.initiativeValue(), a.initiativeValue()));

    return rolls;
  }
}
