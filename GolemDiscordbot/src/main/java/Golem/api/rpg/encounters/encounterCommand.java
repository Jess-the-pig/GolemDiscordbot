package Golem.api.rpg.encounters;

import Golem.api.common.enums.DiscordOptionType;
import Golem.api.common.factories.ApplicationCommandOptionDataFactory;
import Golem.api.common.interfaces.HasOptions;
import Golem.api.common.interfaces.ICommand;
import Golem.api.common.utils.CommandOptionReader;
import Golem.api.rpg.dto.ReplyFactory;
import Golem.api.rpg.encounters.create_encounter.EncounterFactory;
import Golem.api.rpg.encounters.create_encounter.set_difficulty.EncounterDifficulty;
import Golem.api.rpg.encounters.create_encounter.set_difficulty.EncounterEnemySize;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.discordjson.json.ApplicationCommandOptionData;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class encounterCommand implements ICommand, HasOptions {

    private final EncounterFactory encounterFactory;

    @Override
    public String getName() {
        return "encounter";
    }

    // TODO : Mettre en place les Paramètres.
    @Override
    @Transactional
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        CommandOptionReader reader = new CommandOptionReader(event);
        String difficultyStr =
                reader.getOptionOrDefault(
                        "difficulty", ApplicationCommandInteractionOptionValue::asString, "EASY");
        String terrain =
                reader.getOptionOrDefault(
                        "terrain", ApplicationCommandInteractionOptionValue::asString, "forest");
        String enemySizeStr =
                reader.getOptionOrDefault(
                        "enemysize", ApplicationCommandInteractionOptionValue::asString, "SMALL");

        EncounterDifficulty encounterDifficulty =
                EncounterDifficulty.valueOf(difficultyStr.toUpperCase());
        EncounterEnemySize enemySize = EncounterEnemySize.valueOf(enemySizeStr.toUpperCase());

        Encounters generatedEncounter =
                encounterFactory.handle(encounterDifficulty, terrain, enemySize, event);

        return ReplyFactory.reply(event, generatedEncounter.toString());
    }

    @Override
    public Optional<List<ApplicationCommandOptionData>> getOptions() {
        return Optional.of(
                List.of(
                        ApplicationCommandOptionDataFactory.option(
                                DiscordOptionType.STRING,
                                "difficulty",
                                "easy,medium,hard or deadly?",
                                true),
                        ApplicationCommandOptionDataFactory.option(
                                DiscordOptionType.STRING,
                                "terrain",
                                "plains, forest , swamp or other?",
                                true),
                        ApplicationCommandOptionDataFactory.option(
                                DiscordOptionType.STRING,
                                "enemysize",
                                "champion, escoude, detachement or other ?",
                                true)));
    }
}
