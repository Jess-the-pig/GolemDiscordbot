package Golem.api.common.utils;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.interfaces.TimeStampedEntity;
import Golem.api.rpg.dto.ReplyFactory;
import java.util.function.BiConsumer;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/**
 * Gestionnaire d'étape générique avec validation.
 *
 * <p>Cette classe applique un validateur sur le contenu de l'événement, met à jour l'entité via un
 * setter, et envoie soit un message de succès pour l'étape suivante, soit un message d'erreur si la
 * validation échoue.
 *
 * @param <T> le type d'entité traitée, doit étendre {@link TimeStampedEntity}
 * @param <E> le type d'événement transportant le contenu, doit implémenter {@link ContentCarrier}
 * @param <R> le type de valeur validée et appliquée à l'entité
 */
public class GenericValidatedStepHandler<T extends TimeStampedEntity, E extends ContentCarrier, R>
    implements StepHandler<T, E> {

  private final Function<E, R> validator;
  private final BiConsumer<T, R> setter;
  private final String nextPrompt;
  private final String errorPrompt;

  /**
   * Crée un gestionnaire d'étape avec validation.
   *
   * @param validator la fonction de validation prenant l'événement
   * @param setter la fonction appliquant la valeur validée à l'entité
   * @param nextPrompt le message à envoyer en cas de succès
   * @param errorPrompt le message à envoyer en cas d'erreur
   */
  public GenericValidatedStepHandler(
      Function<E, R> validator, BiConsumer<T, R> setter, String nextPrompt, String errorPrompt) {
    this.validator = validator;
    this.setter = setter;
    this.nextPrompt = nextPrompt;
    this.errorPrompt = errorPrompt;
  }

  /**
   * Traite l'étape en appliquant la validation et en envoyant le message approprié.
   *
   * @param event l'événement contenant le contenu
   * @param session la session contenant l'entité et l'état de l'étape
   * @return un {@link Mono} indiquant la complétion du traitement
   */
  @Override
  public Mono<Void> handle(E event, Session<T> session) {
    try {
      R value = validator.apply(event); // ⚡ on passe tout le carrier
      setter.accept(session.entity, value);
      session.step += 1;
      return ReplyFactory.reply(event.getDelegate(), nextPrompt);
    } catch (Exception e) {
      return ReplyFactory.reply(event.getDelegate(), errorPrompt);
    }
  }
}
