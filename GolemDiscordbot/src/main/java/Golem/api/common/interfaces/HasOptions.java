package Golem.api.common.interfaces;

import discord4j.discordjson.json.ApplicationCommandOptionData;
import java.util.List;
import java.util.Optional;

/** Interface pour les objets pouvant fournir des options de commande. */
public interface HasOptions {
  Optional<List<ApplicationCommandOptionData>> getOptions();
}
