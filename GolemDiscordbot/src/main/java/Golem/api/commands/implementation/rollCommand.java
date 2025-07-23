package Golem.api.commands.implementation;

import Golem.api.commands.HasOptions;
import Golem.api.commands.ICommand;
import Golem.api.services.RollService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class rollCommand implements ICommand, HasOptions {
  private final RollService rollService;

  @Override
  public String getName() {
    return "roll";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {

    int times =
        event
            .getOption("times")
            .flatMap(ApplicationCommandInteractionOption::getValue)
            .map(ApplicationCommandInteractionOptionValue::asLong)
            .map(Long::intValue)
            .orElse(1);

    int sides =
        event
            .getOption("sides")
            .flatMap(ApplicationCommandInteractionOption::getValue)
            .map(ApplicationCommandInteractionOptionValue::asLong)
            .map(Long::intValue)
            .orElse(20);

    boolean total =
        event
            .getOption("total")
            .flatMap(ApplicationCommandInteractionOption::getValue)
            .map(ApplicationCommandInteractionOptionValue::asBoolean)
            .orElse(false);

    StringBuilder result = new StringBuilder("Résultats : ");
    int totalCalc = 0;

    for (int i = 0; i < times; i++) {
      int roll = rollService.rollDiceWithResult(sides);
      result.append(roll);
      if (i < times - 1) {
        result.append(" , ");
      }
      totalCalc += roll;
    }

    if (total) {
      result.append("\nTotal : ").append(totalCalc);
    }

    return event.reply(result.toString());
  }

  @Override
  public Optional<List<ApplicationCommandOptionData>> getOptions() {
    return Optional.of(
        List.of(
            ApplicationCommandOptionData.builder()
                .name("times")
                .description("How many dice do you want to roll?")
                .type(4)
                .required(true)
                .build(),
            ApplicationCommandOptionData.builder()
                .name("sides")
                .description("How many sides has your dice?")
                .type(4)
                .required(true)
                .build(),
            ApplicationCommandOptionData.builder()
                .name("total")
                .description("Do you want the total?")
                .type(5)
                .required(true)
                .build()));
  }
}
