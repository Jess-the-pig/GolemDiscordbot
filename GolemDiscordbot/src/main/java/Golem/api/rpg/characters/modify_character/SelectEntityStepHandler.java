package Golem.api.rpg.characters.modify_character;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.interfaces.TimeStampedEntity;
import Golem.api.common.utils.Session;
import Golem.api.rpg.dto.ReplyFactory;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SelectEntityStepHandler<T extends TimeStampedEntity>
    implements StepHandler<T, ContentCarrier> {

  private final Function<String, T> entityFinder;

  @Override
  public Mono<Void> handle(ContentCarrier event, Session<T> session) {
    String content = event.getContent().trim();

    T entity = entityFinder.apply(content);
    if (entity == null) {
      return ReplyFactory.reply(event.getDelegate(), "I couldn't find this entity. Try again?");
    }

    session.entity = entity;
    session.step = 1;

    return ReplyFactory.reply(
        event.getDelegate(), "What do you want to update? (name, race, class, etc.)");
  }
}
