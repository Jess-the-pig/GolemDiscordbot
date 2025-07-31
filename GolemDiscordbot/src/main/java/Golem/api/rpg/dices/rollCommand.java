package Golem.api.rpg.dices;

import Golem.api.common.enums.DiscordOptionType;
import Golem.api.common.factories.ApplicationCommandOptionDataFactory;
import Golem.api.common.interfaces.HasOptions;
import Golem.api.common.interfaces.ICommand;
import Golem.api.common.utils.CommandOptionReader;
import Golem.api.rpg.dices.roll_dices.RollService;
import Golem.api.rpg.dto.ReplyFactory;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
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
  private final RollResultFormatter rollResultFormatter;

  @Override
  public String getName() {
    return "roll";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {

    CommandOptionReader reader = new CommandOptionReader(event);
    Long times =
        reader.getOptionOrDefault("times", ApplicationCommandInteractionOptionValue::asLong, 1L);
    Long sides =
        reader.getOptionOrDefault("sides", ApplicationCommandInteractionOptionValue::asLong, 20L);
    Boolean total =
        reader.getOptionOrDefault(
            "total", ApplicationCommandInteractionOptionValue::asBoolean, false);

    RollResult result = rollService.rollMultipleDice(sides, times);
    String output = rollResultFormatter.format(result, total);

    return ReplyFactory.reply(event, output);
  }

  @Override
  public Optional<List<ApplicationCommandOptionData>> getOptions() {
    return Optional.of(
        List.of(
            ApplicationCommandOptionDataFactory.option(
                DiscordOptionType.INTEGER, "times", "How many dice do you want to roll?", true),
            ApplicationCommandOptionDataFactory.option(
                DiscordOptionType.INTEGER, "sides", "How many sides has your dice?", true),
            ApplicationCommandOptionDataFactory.option(
                DiscordOptionType.BOOLEAN, "total", "Do you want the total?", true)));
  }
}
