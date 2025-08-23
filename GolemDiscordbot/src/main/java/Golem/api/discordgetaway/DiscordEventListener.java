package Golem.api.discordgetaway;

import discord4j.core.event.domain.Event;
import reactor.core.publisher.Mono;

/**
 * Interface fonctionnelle représentant un listener pour un événement Discord.
 *
 * @param <T> le type d'événement Discord étendant {@link Event}
 */
@FunctionalInterface
public interface DiscordEventListener<T extends Event> {
  /**
   * Gère l'événement Discord.
   *
   * @param event l'événement à traiter
   * @return un {@link Mono} indiquant la complétion du traitement
   */
  Mono<Void> handle(T event);
}
