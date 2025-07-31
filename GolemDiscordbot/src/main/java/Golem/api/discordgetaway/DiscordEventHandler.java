package Golem.api.discordgetaway;

import discord4j.core.event.domain.Event;

public record DiscordEventHandler<T extends Event>(
    Class<T> eventClass, DiscordEventListener<T> listener) {}
