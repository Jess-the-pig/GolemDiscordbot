package Golem.api.common.utils;

import Golem.api.common.interfaces.OptionMapper;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandOptionReader {
  private final ChatInputInteractionEvent event;

  public <T> Optional<T> getOption(String name, OptionMapper<T> mapper) {
    return event
        .getOption(name)
        .flatMap(ApplicationCommandInteractionOption::getValue)
        .map(
            value -> {
              try {
                return mapper.map(value);
              } catch (IllegalArgumentException e) {
                return null;
              }
            });
  }

  public <T> T getOptionOrDefault(String name, OptionMapper<T> mapper, T defaultValue) {
    return getOption(name, mapper).orElse(defaultValue);
  }
}
