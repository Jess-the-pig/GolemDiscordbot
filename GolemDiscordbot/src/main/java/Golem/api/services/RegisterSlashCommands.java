package Golem.api.services;

import Golem.api.commands.ICommand;
import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.ApplicationCommandRequest;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class RegisterSlashCommands {

  public void registerSlashCommands(GatewayDiscordClient client, Map<String, ICommand> commands) {
    for (ICommand command : commands.values()) {

      var builder =
          ApplicationCommandRequest.builder()
              .name(command.getName())
              .description("Command description for " + command.getName());

      if (!command.getOptions().isEmpty()) {
        builder.options(command.getOptions());
      }

      ApplicationCommandRequest request = builder.build();

      client
          .getRestClient()
          .getApplicationService()
          .createGlobalApplicationCommand(client.getSelfId().asLong(), request)
          .subscribe();
    }
  }
}
