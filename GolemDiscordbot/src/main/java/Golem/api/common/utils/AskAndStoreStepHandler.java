package Golem.api.common.utils;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.interfaces.TimeStampedEntity;
import Golem.api.rpg.dto.ReplyFactory;
import java.util.function.BiConsumer;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/**
 * StepHandler qui pose une question à l'utilisateur et stocke la réponse dans la session, puis
 * applique la valeur via un setter.
 */
public class AskAndStoreStepHandler<T extends TimeStampedEntity, E extends ContentCarrier<R>, R>
    implements StepHandler<T, E> {

  private final String question;
  private final Function<E, R> converter;
  private final BiConsumer<T, R> setter;

  public AskAndStoreStepHandler(
      String question, Function<E, R> converter, BiConsumer<T, R> setter) {
    this.question = question;
    this.converter = converter;
    this.setter = setter;
  }

  @Override
  public Mono<Void> handle(E event, Session<T> session) {
    try {
      // ⚡ Si aucune donnée n'a été saisie, poser la question
      if (session.data == null) {
        Object delegate = event.getDelegate();
        return ReplyFactory.reply(delegate, question);
      }

      // ⚡ Stocke la réponse
      R value = converter.apply(event);
      setter.accept(session.entity, value);

      // ⚡ Passe à l'étape suivante
      session.step += 1;

      return Mono.empty();
    } catch (Exception e) {
      Object delegate = event.getDelegate();
      return ReplyFactory.reply(delegate, question); // fallback si erreur
    }
  }
}
