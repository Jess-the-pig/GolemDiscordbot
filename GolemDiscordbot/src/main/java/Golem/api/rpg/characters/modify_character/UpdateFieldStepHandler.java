package Golem.api.rpg.characters;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.utils.Session;
import Golem.api.db.CharacterRepository;
import Golem.api.rpg.dto.ReplyFactory;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.BiConsumer;
import reactor.core.publisher.Mono;

public class UpdateFieldStepHandler implements StepHandler<Characters, ContentCarrier> {
  private final CharacterRepository characterRepository;
  private final Map<String, BiConsumer<Characters, String>> stringFieldSetters;

  public UpdateFieldStepHandler(
      CharacterRepository repo, Map<String, BiConsumer<Characters, String>> setters) {
    this.characterRepository = repo;
    this.stringFieldSetters = setters;
  }

  @Override
  public Mono<Void> handle(ContentCarrier event, Session<Characters> session) {
    String newValue = event.getContent().trim();
    String field = session.lastField;

    try {
      if (stringFieldSetters.containsKey(field)) {
        stringFieldSetters.get(field).accept(session.entity, newValue);
      } else if ("level".equals(field)) {
        session.entity.setLevel(Integer.parseInt(newValue));
      } else if ("experiencepoints".equals(field)) {
        session.entity.setExperiencePoints(Integer.parseInt(newValue));
      } else {
        return ReplyFactory.reply(event.getDelegate(), "Unknown field. Try again.");
      }
    } catch (NumberFormatException e) {
      return ReplyFactory.reply(event.getDelegate(), "Please enter a valid number for " + field);
    }

    session.entity.setLastUpdated(LocalDateTime.now());
    characterRepository.save(session.entity);
    session.step = 1; // retour à l'étape choix champ
    return ReplyFactory.reply(event.getDelegate(), "Updated! Anything else? Or type **done**.");
  }
}
