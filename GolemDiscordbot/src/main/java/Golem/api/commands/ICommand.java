package Golem.api.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import java.util.List;
import java.util.Optional;
import reactor.core.publisher.Mono;

public interface ICommand {
  String getName();

  Mono<Void> handle(ChatInputInteractionEvent event);

  Optional<List<ApplicationCommandOptionData>> getOptions();
}
