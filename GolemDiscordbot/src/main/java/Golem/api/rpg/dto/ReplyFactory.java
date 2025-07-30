package Golem.api.rpg.dto;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public class ReplyFactory {
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
