package Golem.api.common.utils;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.db.CharacterRepository;
import Golem.api.rpg.campaign.Campaign;
import Golem.api.rpg.characters.Characters;
import Golem.api.rpg.dto.ReplyFactory;
import java.util.ArrayList;
import java.util.List;
import reactor.core.publisher.Mono;

/**
 * StepHandler pour ajouter plusieurs personnages à une campagne. Permet à l'utilisateur de saisir
 * les noms des personnages un par un, ou plusieurs séparés par des virgules, jusqu'à ce qu'il tape
 * "done".
 */
public class AddPlayersStepHandler implements StepHandler<Campaign, ContentCarrier> {

  private final CharacterRepository characterRepository;

  public AddPlayersStepHandler(CharacterRepository characterRepository) {
    this.characterRepository = characterRepository;
  }

  @Override
  public Mono<Void> handle(ContentCarrier event, Session<Campaign> session) {
    String content = event.getContent().trim();
    Campaign campaign = session.entity;

    if (campaign.getCharacters() == null) {
      campaign.setCharacters(new ArrayList<>());
    }

    if ("done".equalsIgnoreCase(content)) {
      return ReplyFactory.reply(
              event.getDelegate(), "All players added! Tap enter to end campaign creation")
          .doOnSuccess(v -> session.step += 1);
    }

    // Split par virgules pour gérer plusieurs joueurs à la fois
    String[] names = content.split(",");
    List<String> notFound = new ArrayList<>();

    for (String nameRaw : names) {
      String name = nameRaw.trim();
      Characters character = characterRepository.findByCharacterName(name);
      if (character != null) {
        campaign.getCharacters().add(character);
      } else {
        notFound.add(name);
      }
    }

    if (!notFound.isEmpty()) {
      return ReplyFactory.reply(
          event.getDelegate(), "Characters not found: " + String.join(", ", notFound));
    }

    return ReplyFactory.reply(
        event.getDelegate(), "Players added! Type more names or type **done** to finish.");
  }
}
