package Golem.api.common.wrappers;

import Golem.api.common.interfaces.ContentCarrier;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import lombok.RequiredArgsConstructor;

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
