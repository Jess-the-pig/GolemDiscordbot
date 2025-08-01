package Golem.api.common.utils;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.interfaces.TimeStampedEntity;
import Golem.api.rpg.dto.ReplyFactory;
import java.time.LocalDateTime;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FinalStepHandler<T extends TimeStampedEntity, E extends ContentCarrier>
    implements StepHandler<T, E> {

  private final BiConsumer<T, String> setter;
  private final Consumer<T> saveAction;
  private final String successMessage;

  @Override
  public Mono<Void> handle(E event, Session<T> session) {
    String content = event.getContent();
    setter.accept(session.entity, content);

    session.entity.setDateCreated(LocalDateTime.now());
    session.entity.setLastUpdated(LocalDateTime.now());

    saveAction.accept(session.entity);

    return ReplyFactory.reply(event, successMessage);
  }
}
