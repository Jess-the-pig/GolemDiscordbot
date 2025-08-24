package Golem.api.discordgetaway.connection;

import Golem.api.discordgetaway.DiscordEventHandlerProvider;
import Golem.api.discordgetaway.slashcommands.CommandDispatcher;
import Golem.api.discordgetaway.slashcommands.RegisterSlashCommands;
import Golem.api.rpg.campaign.CampaignService;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Service Spring pour gérer le bot Discord.
 *
 * <p>Démarre le bot, enregistre les commandes slash, gère les interactions et publie les événements
 * dans Redis. Fournit également un mécanisme de shutdown propre.
 */
@Service("discordBotServiceV2")
@RequiredArgsConstructor
@Slf4j
public class DiscordBotService {

  @Value("${DISCORD_TOKEN}")
  private String token;

  private GatewayDiscordClient client;

  private final CommandDispatcher dispatcher;
  private final RegisterSlashCommands registerSlashCommands;
  private final List<DiscordEventHandlerProvider> handlerProviders;
  private final StringRedisTemplate redisTemplate;
  private final CampaignService campaignService;

  @PostConstruct
  public void startBot() {
    DiscordClient discordClient = DiscordClient.create(token);
    client = discordClient.login().block();

    if (client != null) {
      log.info("✅ Bot connected as: {}", client.getSelf().block().getUsername());

      // Enregistre toutes les commandes slash
      registerSlashCommands.registerSlashCommands(client, dispatcher.getCommands());
      client.on(MessageCreateEvent.class, campaignService::handleCampaignMessage).subscribe();

      // Handlers du dispatcher
      client.on(ButtonInteractionEvent.class, dispatcher::handleButton).subscribe();

      client
          .on(
              ChatInputInteractionEvent.class,
              event -> {
                String author = event.getInteraction().getUser().getUsername();
                String command = event.getCommandName();
                publishToRedisStream(
                    "discord-events", Map.of("author", author, "command", command));
                return dispatcher.handle(event);
              })
          .subscribe();

      client.onDisconnect().block();
    }
  }

  /** Publie un message dans un stream Redis. */
  private void publishToRedisStream(String streamKey, Map<String, String> message) {
    StreamOperations<String, String, String> streamOps = redisTemplate.opsForStream();
    streamOps.add(streamKey, message);
  }

  /** Arrêt propre du bot. */
  @PreDestroy
  public void shutdown() {
    if (client != null) {
      client.logout().block();
    }
  }
}
