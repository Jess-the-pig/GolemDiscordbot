package Golem.api.common.utils;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.interfaces.TimeStampedEntity;
import Golem.api.rpg.dto.ReplyFactory;
import java.util.function.BiConsumer;
import java.util.function.Function;
import reactor.core.publisher.Mono;

public class GenericValidatedStepHandler<T extends TimeStampedEntity, E extends ContentCarrier, R>
    implements StepHandler<T, E> {

  private final Function<String, R> validator;
  private final BiConsumer<T, R> setter;
  private final String nextPrompt;
  private final String errorPrompt;

  public GenericValidatedStepHandler(
      Function<String, R> validator,
      BiConsumer<T, R> setter,
      String nextPrompt,
      String errorPrompt) {
    this.validator = validator;
    this.setter = setter;
    this.nextPrompt = nextPrompt;
    this.errorPrompt = errorPrompt;
  }

  @Override
  public Mono<Void> handle(E event, Session<T> session) {
    String content = event.getContent();
    try {
      R value = validator.apply(content);
      setter.accept(session.entity, value);
      session.step += 1;
      return ReplyFactory.reply(event.getDelegate(), nextPrompt);
    } catch (Exception e) {
      return ReplyFactory.reply(event.getDelegate(), errorPrompt);
    }
  }
}
