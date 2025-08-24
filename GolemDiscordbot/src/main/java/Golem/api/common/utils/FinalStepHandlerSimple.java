package Golem.api.common.utils;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.interfaces.TimeStampedEntity;
import Golem.api.rpg.dto.ReplyFactory;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import reactor.core.publisher.Mono;

/**
 * Gestionnaire final d'une étape qui sauvegarde l'entité et envoie un message de confirmation. Le
 * setter est optionnel et peut être ignoré.
 *
 * @param <T> le type d'entité traitée, doit étendre {@link TimeStampedEntity}
 * @param <E> le type d'événement transportant le contenu, doit implémenter {@link ContentCarrier}
 */
public class FinalStepHandlerSimple<T extends TimeStampedEntity, E extends ContentCarrier>
    implements StepHandler<T, E> {

  private final Consumer<T> saveAction;
  private final String successMessage;

  /**
   * Constructeur pour un final step handler simple sans setter.
   *
   * @param saveAction action de sauvegarde à appliquer à l'entité
   * @param successMessage message de confirmation à envoyer
   */
  public FinalStepHandlerSimple(Consumer<T> saveAction, String successMessage) {
    this.saveAction = saveAction;
    this.successMessage = successMessage;
  }

  @Override
  public Mono<Void> handle(E event, Session<T> session) {
    // Mettre à jour les timestamps
    session.entity.setDateCreated(LocalDateTime.now());
    session.entity.setLastUpdated(LocalDateTime.now());

    // Sauvegarde de l'entité
    saveAction.accept(session.entity);

    // Envoi du message de confirmation
    return ReplyFactory.reply(event, successMessage);
  }
}
