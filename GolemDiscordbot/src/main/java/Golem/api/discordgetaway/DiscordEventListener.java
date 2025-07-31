package Golem.api.discordgetaway;

import discord4j.core.event.domain.Event;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface DiscordEventListener<T extends Event> {
  Mono<Void> handle(T event);
}
