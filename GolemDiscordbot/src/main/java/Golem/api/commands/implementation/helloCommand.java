package Golem.api.commands.implementation;

import Golem.api.commands.ICommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
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
