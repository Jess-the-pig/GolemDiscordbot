package Golem.api.discordgetaway.connection;

import Golem.api.discordgetaway.slashcommands.CommandDispatcher;
import Golem.api.discordgetaway.slashcommands.RegisterSlashCommands;
import Golem.api.rpg.campaign.CampaignService;
import Golem.api.rpg.characters.consult_characters.CharacterConsultService;
import Golem.api.rpg.characters.create_character.CharacterCreateService;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
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
  private final CharacterCreateService characterCreateService; // Garde un seul service
  private final CampaignService campaignService;
  private final CharacterConsultService characterConsultService;

  private final StringRedisTemplate redisTemplate; // Injection correcte

  /**
   * Démarre le bot Discord après l'initialisation du service Spring.
   *
   * <p>Connecte le bot, enregistre les commandes, gère les événements de bouton et de commande, et
   * publie les événements dans Redis.
   */
  @PostConstruct
  public void startBot() {
    DiscordClient discordClient = DiscordClient.create(token);
    client = discordClient.login().block();

    if (client != null) {
      log.info("Bot connected as: {}", client.getSelf().block().getUsername());

      registerSlashCommands.registerSlashCommands(client, dispatcher.getCommands());

      client.on(ButtonInteractionEvent.class, dispatcher::handleButton).subscribe(event -> {});

      client
          .on(ChatInputInteractionEvent.class)
          .subscribe(
              event -> {
                String content = event.getCommandName(); // nom de la commande
                String author =
                    event
                        .getInteraction()
                        .getUser()
                        .getUsername(); // récupère directement le username

                publishToRedisStream(
                    "discord-events", Map.of("author", author, "command", content));
              });
      // Bloque jusqu'à déconnexion
      client.onDisconnect().block();
    }
  }

  /**
   * Publie un message dans un stream Redis.
   *
   * @param streamKey la clé du stream Redis
   * @param message le message à publier sous forme de map
   */
  private void publishToRedisStream(String streamKey, Map<String, String> message) {
    StreamOperations<String, String, String> streamOps = redisTemplate.opsForStream();
    streamOps.add(streamKey, message);
  }

  /** Effectue un arrêt propre du bot lors de la destruction du service. */
  @PreDestroy
  public void shutdown() {
    if (client != null) {
      client.logout().block();
    }
  }
}
