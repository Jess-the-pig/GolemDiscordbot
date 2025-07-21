package Golem.api.commands;

import discord4j.discordjson.json.ApplicationCommandOptionData;
import java.util.List;
import java.util.Optional;

public interface HasOptions {
  Optional<List<ApplicationCommandOptionData>> getOptions();
}
