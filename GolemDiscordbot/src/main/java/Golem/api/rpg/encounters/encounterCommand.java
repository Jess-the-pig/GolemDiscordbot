package Golem.api.rpg.encounters;

import Golem.api.common.commands.HasOptions;
import Golem.api.common.commands.ICommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class encounterCommand implements ICommand, HasOptions {

  @Override
  public Optional<List<ApplicationCommandOptionData>> getOptions() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getOptions'");
  }

  @Override
  public String getName() {
    return "encounter";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'handle'");
  }
}
