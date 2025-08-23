package Golem.api.common.interfaces;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

/** Interface représentant une commande pouvant être exécutée. */
public interface ICommand {
  String getName();

  /**
   * Traite l'exécution de la commande lors d'une interaction utilisateur.
   *
   * @param event l'événement d'interaction de commande
   * @return un {@link Mono} indiquant la complétion du traitement
   */
  Mono<Void> handle(ChatInputInteractionEvent event);
}
