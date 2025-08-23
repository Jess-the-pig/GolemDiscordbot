package Golem.api.common.interfaces;

import Golem.api.common.utils.Session;
import reactor.core.publisher.Mono;

/**
 * Interface pour gérer une étape d'un processus sur des entités horodatées.
 *
 * @param <T> le type d'entité traitée, doit étendre {@link TimeStampedEntity}
 * @param <E> le type d'événement transportant le contenu, doit implémenter {@link ContentCarrier}
 */
public interface StepHandler<T extends TimeStampedEntity, E extends ContentCarrier> {
  /**
   * Traite une étape pour l'entité et la session fournies à partir de l'événement.
   *
   * @param event l'événement contenant le contenu
   * @param session la session associée à l'entité
   * @return un {@link Mono} indiquant la complétion du traitement
   */
  Mono<Void> handle(E event, Session<T> session);
}
