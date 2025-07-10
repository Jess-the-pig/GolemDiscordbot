package Golem.api.services;

import Golem.api.commands.ICommand;

import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.ApplicationCommandRequest;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RegisterSlashCommands {

    // Enregistre les commandes Slash à partir du Map des commandes
    public void registerSlashCommands(GatewayDiscordClient client, Map<String, ICommand> commands) {
        for (ICommand command : commands.values()) {
            ApplicationCommandRequest request =
                    ApplicationCommandRequest.builder()
                            .name(command.getName()) // Nom de la commande, p. ex. "ping"
                            .description(
                                    "Command description for "
                                            + command.getName()) // Description dynamique
                            .build();

            client.getRestClient()
                    .getApplicationService()
                    .createGlobalApplicationCommand(client.getSelfId().asLong(), request)
                    .subscribe();
        }
    }
}
