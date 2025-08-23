package Golem.api.common.wrappers;

import Golem.api.common.interfaces.ContentCarrier;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.RequiredArgsConstructor;

/**
 * Wrapper pour un {@link MessageCreateEvent} implémentant {@link ContentCarrier}. Fournit un accès
 * au contenu du message et à l'événement délégué.
 */
@RequiredArgsConstructor
public class MessageCreateEventWrapper implements ContentCarrier {
  private final MessageCreateEvent event;

  @Override
  public String getContent() {
    return event.getMessage().getContent();
  }

  public MessageCreateEvent getDelegate() {
    return event;
  }
}
