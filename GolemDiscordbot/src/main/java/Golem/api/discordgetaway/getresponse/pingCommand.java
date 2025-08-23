package Golem.api.discordgetaway.getresponse;

import Golem.api.common.interfaces.ICommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Commande Discord simple pour tester la latence du bot.
 *
 * <p>Répond "🏓Pong!" lorsqu'elle est exécutée.
 */
@Component
public class pingCommand implements ICommand {

  @Override
  public String getName() {
    return "ping";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return event.reply("🏓Pong!");
  }
}
