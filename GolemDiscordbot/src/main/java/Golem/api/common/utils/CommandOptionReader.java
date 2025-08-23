package Golem.api.common.utils;

import Golem.api.common.interfaces.OptionMapper;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

/** Classe utilitaire pour lire et mapper les options d'une commande. */
@RequiredArgsConstructor
public class CommandOptionReader {
  private final ChatInputInteractionEvent event;

  /**
   * Récupère et mappe une option spécifique de la commande.
   *
   * @param <T> le type de retour après mapping
   * @param name le nom de l'option
   * @param mapper le mapper pour convertir la valeur de l'option
   * @return un {@link Optional} contenant la valeur mappée si présente et valide
   */
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

  /**
   * Récupère et mappe une option spécifique de la commande ou retourne une valeur par défaut.
   *
   * @param <T> le type de retour après mapping
   * @param name le nom de l'option
   * @param mapper le mapper pour convertir la valeur de l'option
   * @param defaultValue la valeur par défaut si l'option n'est pas présente ou invalide
   * @return la valeur mappée ou la valeur par défaut
   */
  public <T> T getOptionOrDefault(String name, OptionMapper<T> mapper, T defaultValue) {
    return getOption(name, mapper).orElse(defaultValue);
  }
}
