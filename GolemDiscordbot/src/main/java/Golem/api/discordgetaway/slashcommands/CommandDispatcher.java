package Golem.api.discordgetaway.slashcommands;

import Golem.api.common.interfaces.HasButtons;
import Golem.api.common.interfaces.ICommand;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CommandDispatcher {

  private final Map<String, ICommand> commands = new HashMap<>();
  private final Map<String, ICommand> buttonHandlers = new HashMap<>();

  public CommandDispatcher(List<ICommand> commandList) {
    for (ICommand cmd : commandList) {
      commands.put(cmd.getName(), cmd);

      // Si la commande a des boutons, on récupère les customId automatiquement
      if (cmd instanceof HasButtons) {
        HasButtons withButtons = (HasButtons) cmd;

        // On suppose que HasButtons a une méthode getCustomIds() qui retourne tous les customId
        withButtons
            .getCustomIds()
            .forEach(
                customId -> {
                  buttonHandlers.put(customId, cmd);
                });
      }
    }
  }

  public Mono<Void> handle(ChatInputInteractionEvent event) {
    String name = event.getCommandName();
    log.info("Chargement du handler pour la commande '{}'", name);

    ICommand command = commands.get(name);
    if (command != null) {
      return command.handle(event);
    }

    return event.reply("Commande inconnue").withEphemeral(true);
  }

  public Mono<Void> handleButton(ButtonInteractionEvent event) {
    log.info("ButtonInteractionEvent received: {}", event);
    String customId = event.getCustomId();

    ICommand command = buttonHandlers.get(customId);
    if (command instanceof HasButtons) {
      return ((HasButtons) command).handleButtonInteraction(event);
    }

    return event.reply("Aucun handler pour ce bouton.").withEphemeral(true);
  }

  public Map<String, ICommand> getCommands() {
    return commands;
  }
}
