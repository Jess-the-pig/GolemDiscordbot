package Golem.api;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class DiscordBotService {

    @Value(
            "MTM2MTQxMzA4MzEyMTMxOTkzNg.GsavBl.H7WmTwgtCri-b5DQOZ1rPIAusoP6uGRsaGIIUY") // Utilise
                                                                                        // le token
                                                                                        // depuis
                                                                                        // application.properties
    private String token; // Le token de ton bot Discord

    private GatewayDiscordClient client;
    private ExecutorService executorService;

    // Méthode appelée lors de l'initialisation du service (après la construction de l'objet)
    @PostConstruct
    public void startBot() {
        // Créer un service d'exécution pour gérer l'exécution asynchrone
        executorService = Executors.newSingleThreadExecutor();

        // Démarrer le client Discord dans un thread séparé
        executorService.submit(
                () -> {
                    // Créer le client Discord avec le token
                    DiscordClient discordClient = DiscordClient.create(token);
                    client = discordClient.login().block();

                    // Si le client Discord est correctement connecté
                    if (client != null) {
                        // Mise à jour du statut du bot
                        client.updatePresence(
                                        ClientPresence.online(
                                                ClientActivity.listening("to /Golem")))
                                .block();

                        // Écoute des messages entrants
                        client.on(MessageCreateEvent.class)
                                .doOnNext(
                                        event -> {
                                            System.out.println(
                                                    "Message reçu: "
                                                            + event.getMessage().getContent());
                                        })
                                .filter(
                                        event ->
                                                !event.getMessage()
                                                        .getAuthor()
                                                        .map(user -> user.isBot())
                                                        .orElse(true)) // Ignore les messages du bot
                                .map(MessageCreateEvent::getMessage)
                                .flatMap(
                                        message -> {
                                            if (message.getContent().equals("!ping")) {
                                                return message.getChannel()
                                                        .flatMap(
                                                                channel ->
                                                                        channel.createMessage(
                                                                                "Pong!"));
                                            }
                                            return Mono.empty();
                                        })
                                .subscribe(); // Écoute en continu des événements de messages
                    }
                });
    }

    // Méthode pour gérer l'arrêt de l'exécution lorsque l'application Spring Boot se termine
    @PostConstruct
    public void shutdown() {
        // Lors de l'arrêt du contexte Spring, fermer le service d'exécution
        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread(
                                () -> {
                                    if (executorService != null && !executorService.isShutdown()) {
                                        executorService.shutdown();
                                    }
                                }));
    }
}
