package Golem.api.rpg.dto;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public class ReplyFactory {
  public static Mono<Void> reply(Object event, String message) {
    if (event instanceof MessageCreateEvent) {
      return reply((MessageCreateEvent) event, message);
    } else if (event instanceof ButtonInteractionEvent) {
      return reply((ButtonInteractionEvent) event, message);
    } else if (event instanceof ChatInputInteractionEvent) {
      return reply((ChatInputInteractionEvent) event, message);
    } else {
      return Mono.error(
          new IllegalArgumentException("Unsupported event type: " + event.getClass()));
    }
  }

  public static Mono<Void> reply(MessageCreateEvent event, String message) {
    return event
        .getMessage()
        .getChannel()
        .flatMap(channel -> channel.createMessage(message))
        .then();
  }

  public static Mono<Void> reply(ButtonInteractionEvent event, String message) {
    return event.reply(message).withEphemeral(true).then();
  }

  public static Mono<Void> reply(ChatInputInteractionEvent event, String message) {
    return event.reply(message).withEphemeral(true).then();
  }

  public static Mono<Void> deferAndSend(ButtonInteractionEvent event, String message) {
    return event
        .deferReply()
        .withEphemeral(true)
        .then(
            event
                .getInteraction()
                .getChannel()
                .flatMap(channel -> channel.createMessage(message))
                .then());
  }
}
