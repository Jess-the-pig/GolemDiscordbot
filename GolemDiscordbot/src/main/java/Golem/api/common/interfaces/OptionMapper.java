package Golem.api.common.interfaces;

import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;

/**
 * Interface fonctionnelle pour mapper une valeur d'option de commande en un type spécifique.
 *
 * @param <T> le type de sortie attendu après le mapping
 */
@FunctionalInterface
public interface OptionMapper<T> {
  /**
   * Convertit une {@link ApplicationCommandInteractionOptionValue} en un objet du type {@code T}.
   *
   * @param value la valeur de l'option à mapper
   * @return l'objet converti de type {@code T}
   * @throws IllegalArgumentException si la valeur ne peut pas être convertie
   */
  T map(ApplicationCommandInteractionOptionValue value) throws IllegalArgumentException;
}
