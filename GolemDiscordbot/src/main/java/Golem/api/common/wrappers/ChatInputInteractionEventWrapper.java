package Golem.api.common.wrappers;

import Golem.api.common.interfaces.ContentCarrier;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import lombok.RequiredArgsConstructor;

/**
 * Wrapper pour un {@link ChatInputInteractionEvent} implémentant {@link ContentCarrier}. Fournit un
 * accès au contenu de la première option et à l'événement délégué.
 */
@RequiredArgsConstructor
public class ChatInputInteractionEventWrapper implements ContentCarrier {
  private final ChatInputInteractionEvent event;

  @Override
  public String getContent() {
    return event.getOptions().stream()
        .findFirst()
        .flatMap(option -> option.getValue().map(value -> value.asString()))
        .orElse("");
  }

  @Override
  public Object getDelegate() {
    return event;
  }
}
