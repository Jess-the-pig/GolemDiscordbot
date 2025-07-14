package Golem.api.commands.implementation;

import Golem.api.commands.ICommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public class pingCommand implements ICommand {

  @Override
  public String getName() {
    return "ping";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return event.reply("🏓Pong!");
  }
}
