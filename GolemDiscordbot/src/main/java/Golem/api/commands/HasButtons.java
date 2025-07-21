package Golem.api.commands;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import reactor.core.publisher.Mono;

public interface HasButtons {
  Mono<Void> handleButtonInteraction(ButtonInteractionEvent event);
}
