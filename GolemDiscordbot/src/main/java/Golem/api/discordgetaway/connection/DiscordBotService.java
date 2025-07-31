package Golem.api.discordgetaway.connection;

import Golem.api.discordgetaway.DiscordEventHandler;
import Golem.api.discordgetaway.slashcommands.CommandDispatcher;
import Golem.api.discordgetaway.slashcommands.RegisterSlashCommands;
import Golem.api.rpg.campaign.CampaignService;
import Golem.api.rpg.characters.create_character.CharacterCreateService;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("discordBotServiceV2")
@RequiredArgsConstructor
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

  // Méthode appelée lors de l'initialisation du service (après la construction de l'objet)
  @PostConstruct
  public void startBot() {
    executorService = Executors.newSingleThreadExecutor();
    executorService.submit(
        () -> {
          DiscordClient discordClient = DiscordClient.create(token);
          client = discordClient.login().block();

          if (client != null) {
            registerSlashCommands.registerSlashCommands(client, dispatcher.getCommands());
            status.online();

            // Handlers fixes
            client.on(ButtonInteractionEvent.class, dispatcher::handleButton).subscribe();
            client.on(ChatInputInteractionEvent.class, dispatcher::handle).subscribe();

            // Regrouper tous les handlers dynamiquement
            List<List<DiscordEventHandler<?>>> allHandlers =
                List.of(characterService.getEventHandlers());

            // Flatten + enregistrer
            allHandlers.stream().flatMap(List::stream).forEach(handler -> registerHandler(handler));
          }
        });
  }

  private <T extends Event> void registerHandler(DiscordEventHandler<T> handler) {
    client.on(handler.eventClass(), handler.listener()::handle).subscribe();
  }

  // Méthode pour gérer l'arrêt de l'exécution lorsque l'application Spring Boot se termine
  @PreDestroy
  public void shutdown() {
    if (client != null) {
      client.logout().block();
    }
  }
}
