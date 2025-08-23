package Golem.api.common.factories;

import Golem.api.common.enums.DiscordOptionType;
import discord4j.discordjson.json.ApplicationCommandOptionData;

/** Factory pour créer des instances de {@link ApplicationCommandOptionData}. */
public class ApplicationCommandOptionDataFactory {
  /**
   * Crée une option de commande avec les paramètres spécifiés.
   *
   * @param type le type de l'option {@link DiscordOptionType}
   * @param name le nom de l'option
   * @param description la description de l'option
   * @param required indique si l'option est obligatoire
   * @return une instance construite de {@link ApplicationCommandOptionData}
   */
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
