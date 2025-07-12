package Golem.api.services;

import Golem.api.utils.BotStatus;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("discordBotServiceV2")
public class DiscordBotService {

  @Value("MTM2MTQxMzA4MzEyMTMxOTkzNg.GsavBl.H7WmTwgtCri-b5DQOZ1rPIAusoP6uGRsaGIIUY") // Utilise
  private String token; // Le token de ton bot Discord

  private ExecutorService executorService;
  private BotStatus status;
  private GatewayDiscordClient client;
  private final CommandDispatcher dispatcher;
  private final RegisterSlashCommands registerSlashCommands;

  // Méthode appelée lors de l'initialisation du service (après la construction de l'objet)
  @PostConstruct
  public void startBot() {

    executorService = Executors.newSingleThreadExecutor();

    executorService.submit(
        () -> {
          // Créer le client Discord avec le token
          DiscordClient discordClient = DiscordClient.create(token);
          client = discordClient.login().block();

          // Si le client Discord est correctement connecté
          if (client != null) {
            status = new BotStatus(client);
            status.online();
            registerSlashCommands.registerSlashCommands(client, dispatcher.getCommands());
            client.on(ChatInputInteractionEvent.class, dispatcher::handle).subscribe();
          }
        });
  }

  // Méthode pour gérer l'arrêt de l'exécution lorsque l'application Spring Boot se termine
  @PreDestroy
  public void shutdown() {
    if (client != null) {
      client.logout().block();
    }
  }
}
