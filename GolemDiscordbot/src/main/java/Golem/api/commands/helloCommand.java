package Golem.api.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public class helloCommand implements ICommand {

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return "hello";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return event.reply("Hello, I'm Golem ! Your personnal Tabletop Gaming assistant");
  }
}
