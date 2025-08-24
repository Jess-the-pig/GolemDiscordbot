package Golem.api.common.wrappers;

import Golem.api.common.interfaces.ContentCarrier;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;

/**
 * Wrapper pour un {@link ButtonInteractionEvent} implémentant {@link ContentCarrier}. Fournit un
 * accès au contenu et à l'événement délégué.
 */
public class ButtonInteractionEventWrapper implements ContentCarrier<ButtonInteractionEvent> {
  private final ButtonInteractionEvent event;

  public ButtonInteractionEventWrapper(ButtonInteractionEvent event) {
    this.event = event;
  }

  @Override
  public String getContent() {
    return event.getCustomId(); // ou autre contenu pertinent
  }

  @Override
  public Object getDelegate() {
    return event; // ⚡ retourne l'objet original
  }
}
