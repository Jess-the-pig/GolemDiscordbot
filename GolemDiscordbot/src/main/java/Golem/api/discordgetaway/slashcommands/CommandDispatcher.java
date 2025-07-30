package Golem.api.discordgetaway.slashcommands;

import Golem.api.common.commands.HasButtons;
import Golem.api.common.commands.ICommand;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
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

  public Mono<Void> handleButton(ButtonInteractionEvent event) {
    String customId = event.getCustomId();
    String prefix = customId.split(":")[0];

    ICommand command = commands.get(prefix);

    if (command instanceof HasButtons) {
      return ((HasButtons) command).handleButtonInteraction(event);
    }

    return event.reply("Aucun handler pour ce bouton.").withEphemeral(true);
  }

  // Méthode pour obtenir les commandes
  public Map<String, ICommand> getCommands() {
    return commands;
  }
}
