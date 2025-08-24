package Golem.api.discordgetaway;

import discord4j.core.event.domain.Event;
import java.util.function.Function;
import reactor.core.publisher.Mono;

/**
 * Représente un handler pour un événement Discord spécifique.
 *
 * @param <T> le type d'événement Discord étendant {@link Event}
 * @param eventClass la classe de l'événement à écouter
 * @param listener l'instance de {@link DiscordEventListener} pour traiter l'événement
 */
public class DiscordEventHandler<T> {

  private final Class<T> eventType;
  private final Function<T, Mono<Void>> handler;

  public DiscordEventHandler(Class<T> eventType, Function<T, Mono<Void>> handler) {
    this.eventType = eventType;
    this.handler = handler;
  }

  public Class<T> getEventType() {
    return eventType;
  }

  public Function<T, Mono<Void>> getHandler() {
    return handler;
  }
}
