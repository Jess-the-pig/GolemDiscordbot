package Golem.api;

import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
public class CommandRegistrar {

    private final GatewayDiscordClient client;

    public CommandRegistrar(GatewayDiscordClient client) {
        this.client = client;
    }

    @PostConstruct
    public void registerCommands() {
        ApplicationCommandRequest greetCommand = ApplicationCommandRequest.builder()
            .name("greet")
            .description("Say hello to someone")
            .addOption(ApplicationCommandOptionData.builder()
                .name("name")
                .description("The name to greet")
                .type(3) // 3 = STRING type
                .required(true)
                .build())
            .build();

        client.getRestClient().getApplicationService()
            .createGlobalApplicationCommand(client.getRestClient().getApplicationId().block(), greetCommand)
            .doOnSuccess(cmd -> System.out.println("Commande /greet enregistrée"))
            .doOnError(err -> System.err.println("Erreur lors de l'enregistrement de la commande : " + err.getMessage()))
            .subscribe();
    }
}
