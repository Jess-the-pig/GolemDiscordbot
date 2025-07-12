/* SimpleBot Fonctionne , probleme de synchronisation entre SpringBoot et Discord4J

package Golem.api;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public class SimpleBot {
    public static void main(String[] args) {
        // Crée un client Discord
        DiscordClient client =
                DiscordClient.create(
                        "MTM2MTQxMzA4MzEyMTMxOTkzNg.GsavBl.H7WmTwgtCri-b5DQOZ1rPIAusoP6uGRsaGIIUY");

        client.login()
                .doOnTerminate(() -> System.out.println("Bot connecté"))
                .flatMap(
                        gatewayClient -> {
                            // Écoute les événements de création de messages
                            gatewayClient
                                    .on(MessageCreateEvent.class)
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
                                                            .orElse(true)) // Ignore les messages du
                                    // bot
                                    .map(MessageCreateEvent::getMessage)
                                    .flatMap(
                                            message -> {
                                                // Si le message est !ping, répond par Pong!
                                                if (message.getContent().equals("!ping")) {
                                                    return message.getChannel()
                                                            .flatMap(
                                                                    channel ->
                                                                            channel.createMessage(
                                                                                    "Pong!"));
                                                }
                                                return Mono.empty();
                                            })
                                    .subscribe();

                            return Mono.never(); // Bloque le programme
                        })
                .block();
    }
}
*/
