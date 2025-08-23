package Golem.api.common.interfaces;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import reactor.core.publisher.Mono;

/** Interface pour les objets qui peuvent gérer des interactions de boutons. */
public interface HasButtons {
  Mono<Void> handleButtonInteraction(ButtonInteractionEvent event);
}
