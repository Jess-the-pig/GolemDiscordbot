package Golem.api.discordgetaway;

import discord4j.core.event.domain.Event;

/**
 * Représente un handler pour un événement Discord spécifique.
 *
 * @param <T> le type d'événement Discord étendant {@link Event}
 * @param eventClass la classe de l'événement à écouter
 * @param listener l'instance de {@link DiscordEventListener} pour traiter l'événement
 */
public record DiscordEventHandler<T extends Event>(
    Class<T> eventClass, DiscordEventListener<T> listener) {}
