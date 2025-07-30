package Golem.api.rpg.loot.generatechest;

import Golem.api.common.commands.HasOptions;
import Golem.api.common.commands.ICommand;
import Golem.api.rpg.loot.generateeuipements.EquipementService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class chestCommand implements ICommand, HasOptions {
  private EquipementService equipementService;

  @Override
  public Optional<List<ApplicationCommandOptionData>> getOptions() {
    return Optional.of(
        List.of(
            ApplicationCommandOptionData.builder()
                .name("amount")
                .description("How many items (max value possible)")
                .type(4)
                .required(true)
                .build(),
            ApplicationCommandOptionData.builder()
                .name("level")
                .description("which level?")
                .type(4)
                .required(true)
                .build(),
            ApplicationCommandOptionData.builder()
                .name("amountofchests")
                .description("How many chests ?")
                .type(4)
                .required(true)
                .build()));
  }

  @Override
  public String getName() {
    return "chest";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    int amount =
        event
            .getOption("amount")
            .flatMap(ApplicationCommandInteractionOption::getValue)
            .map(ApplicationCommandInteractionOptionValue::asLong)
            .map(Long::intValue)
            .orElse(1);

    int level =
        event
            .getOption("level")
            .flatMap(ApplicationCommandInteractionOption::getValue)
            .map(ApplicationCommandInteractionOptionValue::asLong)
            .map(Long::intValue)
            .orElse(1);

    int amountofchests =
        event
            .getOption("amountofchests")
            .flatMap(ApplicationCommandInteractionOption::getValue)
            .map(ApplicationCommandInteractionOptionValue::asLong)
            .map(Long::intValue)
            .orElse(1);

    long campaignId = event.getInteraction().getChannelId().asLong();

    equipementService.handleChestGeneration(event, amount, amountofchests, level, campaignId);

    return equipementService.handleChestGeneration(
        event, amount, amountofchests, level, campaignId);
  }
}
