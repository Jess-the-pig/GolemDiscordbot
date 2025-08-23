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

/**
 * Gestionnaire final d'une étape qui applique une action sur une entité horodatée, sauvegarde
 * l'entité et envoie un message de confirmation.
 *
 * @param <T> le type d'entité traitée, doit étendre {@link TimeStampedEntity}
 * @param <E> le type d'événement transportant le contenu, doit implémenter {@link ContentCarrier}
 */
@RequiredArgsConstructor
public class FinalStepHandler<T extends TimeStampedEntity, E extends ContentCarrier>
    implements StepHandler<T, E> {

  private final BiConsumer<T, String> setter;
  private final Consumer<T> saveAction;
  private final String successMessage;

  /**
   * Traite l'étape finale pour l'entité et la session données. Applique le setter, met à jour les
   * timestamps, sauvegarde l'entité et envoie un message de confirmation.
   *
   * @param event l'événement contenant le contenu
   * @param session la session contenant l'entité à modifier
   * @return un {@link Mono} indiquant la complétion du traitement et l'envoi du message
   */
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
