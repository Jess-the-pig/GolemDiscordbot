package Golem.api.common.wrappers;

import Golem.api.common.interfaces.ContentCarrier;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ButtonInteractionEventWrapper implements ContentCarrier {
  private final ButtonInteractionEvent event;

  @Override
  public String getContent() {
    return event.getCustomId();
  }

  public ButtonInteractionEvent getDelegate() {
    return event;
  }
}
