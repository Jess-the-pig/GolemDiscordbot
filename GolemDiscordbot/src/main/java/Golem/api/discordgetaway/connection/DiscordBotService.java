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
import discord4j.core.event.domain.message.MessageCreateEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("discordBotServiceV2")
@RequiredArgsConstructor
@Slf4j
public class DiscordBotService {

  @Value("${DISCORD_TOKEN}") // Utilise
  private String token; // Le token de ton bot Discord

  private ExecutorService executorService;
  private BotStatus status;
  private GatewayDiscordClient client;
  private final CommandDispatcher dispatcher;
  private final RegisterSlashCommands registerSlashCommands;
  private final CharacterCreateService characterService;
  private final CampaignService campaignService;
  private final CharacterConsultService characterConsultService;
  private final CharacterCreateService characterCreateService;

  // Méthode appelée lors de l'initialisation du service (après la construction de l'objet)

  @PostConstruct
  public void startBot() {
    DiscordClient discordClient = DiscordClient.create(token);
    client = discordClient.login().block();

    if (client != null) {
      log.info("Bot connected as: {}", client.getSelf().block().getUsername());

      registerSlashCommands.registerSlashCommands(client, dispatcher.getCommands());

      client.on(ButtonInteractionEvent.class, dispatcher::handleButton).subscribe();
      client.on(ChatInputInteractionEvent.class, dispatcher::handle).subscribe();
      client.on(MessageCreateEvent.class, characterCreateService::handleMessageCreate).subscribe();

      // Bloque jusqu'à ce que le bot se déconnecte
      client.onDisconnect().block();
    }
  }

  // Méthode pour gérer l'arrêt de l'exécution lorsque l'application Spring Boot se termine
  @PreDestroy
  public void shutdown() {
    if (client != null) {
      client.logout().block();
    }
  }
}
