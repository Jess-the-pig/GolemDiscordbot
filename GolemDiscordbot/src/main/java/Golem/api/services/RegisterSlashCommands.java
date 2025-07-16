package Golem.api.services;

import Golem.api.commands.ICommand;
import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.ApplicationCommandRequest;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RegisterSlashCommands {

  public void registerSlashCommands(GatewayDiscordClient client, Map<String, ICommand> commands) {
    for (ICommand command : commands.values()) {
      log.info("j'enregistre la commande " + command.getName().toString());
      var builder =
          ApplicationCommandRequest.builder()
              .name(command.getName())
              .description("Command description for " + command.getName());
      command.getOptions().ifPresent(builder::options);

      ApplicationCommandRequest request = builder.build();

      client
          .getRestClient()
          .getApplicationService()
          .createGuildApplicationCommand(client.getSelfId().asLong(), 1385169398876344320L, request)
          .subscribe();
    }
  }
}
