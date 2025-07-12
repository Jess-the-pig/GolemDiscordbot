package Golem.api.services;

import Golem.api.commands.ICommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CommandDispatcher {

  private final Map<String, ICommand> commands = new HashMap<>();

  // Initialisation du Map à partir de la List<ICommand>
  public CommandDispatcher(List<ICommand> commandList) {
    for (ICommand cmd : commandList) {
      commands.put(cmd.getName(), cmd);
    }
  }

  public Mono<Void> handle(ChatInputInteractionEvent event) {
    String name = event.getCommandName();

    // Recherche de la commande dans la Map
    ICommand command = commands.get(name);

    if (command != null) {
      return command.handle(event);
    }

    return event.reply("Commande inconnue").withEphemeral(true);
  }

  // Méthode pour obtenir les commandes
  public Map<String, ICommand> getCommands() {
    return commands;
  }
}
