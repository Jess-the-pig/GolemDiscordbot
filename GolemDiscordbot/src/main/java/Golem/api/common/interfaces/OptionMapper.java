package Golem.api.common.interfaces;

import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;

// Strategy pour conversion
@FunctionalInterface
public interface OptionMapper<T> {
  T map(ApplicationCommandInteractionOptionValue value) throws IllegalArgumentException;
}
