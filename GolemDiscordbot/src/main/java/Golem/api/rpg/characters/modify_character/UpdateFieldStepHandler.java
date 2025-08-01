package Golem.api.rpg.characters.modify_character;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.interfaces.TimeStampedEntity;
import Golem.api.common.utils.Session;
import Golem.api.rpg.dto.ReplyFactory;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateFieldStepHandler<T extends TimeStampedEntity>
    implements StepHandler<T, ContentCarrier> {

  private final Consumer<T> saveAction;
  private final Map<String, BiConsumer<T, Object>> fieldSetters;

  @Override
  public Mono<Void> handle(ContentCarrier event, Session<T> session) {
    String newValue = event.getContent().trim();
    String field = session.lastField;

    try {
      if (fieldSetters.containsKey(field)) {
        // Appliquer le setter universel
        fieldSetters.get(field).accept(session.entity, newValue);
      } else {
        return ReplyFactory.reply(event.getDelegate(), "Unknown field. Try again.");
      }
    } catch (NumberFormatException e) {
      return ReplyFactory.reply(
          event.getDelegate(), "Invalid number format for field **" + field + "**");
    }

    session.entity.setLastUpdated(LocalDateTime.now());
    saveAction.accept(session.entity);
    session.step = 1; // retour au choix du champ
    return ReplyFactory.reply(event.getDelegate(), "Updated! Anything else? Or type **done**.");
  }
}
