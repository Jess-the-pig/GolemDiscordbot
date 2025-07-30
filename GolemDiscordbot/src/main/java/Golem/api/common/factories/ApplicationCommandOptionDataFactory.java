package Golem.api.common.factories;

import Golem.api.common.enums.DiscordOptionType;
import discord4j.discordjson.json.ApplicationCommandOptionData;

public class ApplicationCommandOptionDataFactory {
  public static ApplicationCommandOptionData option(
      DiscordOptionType type, String name, String description, boolean required) {
    return ApplicationCommandOptionData.builder()
        .name(name)
        .description(description)
        .type(type.getCode())
        .required(required)
        .build();
  }
}
