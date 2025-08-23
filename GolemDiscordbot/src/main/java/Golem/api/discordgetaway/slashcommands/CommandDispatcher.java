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

/**
 * Service pour dispatcher les commandes et gérer les interactions avec Discord.
 *
 * <p>Mappe les noms de commandes aux instances {@link ICommand} et gère les interactions des
 * commandes slash ainsi que les boutons.
 */
@Service
@Slf4j
public class CommandDispatcher {

  private final Map<String, ICommand> commands = new HashMap<>();

  /**
   * Initialise le dispatcher à partir d'une liste de commandes.
   *
   * @param commandList la liste des commandes à enregistrer
   */
  public CommandDispatcher(List<ICommand> commandList) {
    for (ICommand cmd : commandList) {
      commands.put(cmd.getName(), cmd);
    }
  }

  /**
   * Traite une interaction de commande slash.
   *
   * @param event l'événement d'interaction de commande
   * @return un {@link Mono} indiquant la complétion du traitement
   */
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    log.info("ButtonInteractionEvent received: {}", event);
    String name = event.getCommandName();
    log.info("chargement du handler" + name);

    ICommand command = commands.get(name);

    if (command != null) {
      return command.handle(event);
    }

    return event.reply("Commande inconnue").withEphemeral(true);
  }

  /**
   * Traite une interaction de bouton.
   *
   * @param event l'événement d'interaction de bouton
   * @return un {@link Mono} indiquant la complétion du traitement
   */
  public Mono<Void> handleButton(ButtonInteractionEvent event) {
    log.info("ButtonInteractionEvent received: {}", event);
    String customId = event.getCustomId();
    String prefix = customId.split(":")[0];

    ICommand command = commands.get(prefix);

    if (command instanceof HasButtons) {
      return ((HasButtons) command).handleButtonInteraction(event);
    }

    return event.reply("Aucun handler pour ce bouton.").withEphemeral(true);
  }

  /**
   * Retourne la map des commandes enregistrées.
   *
   * @return la map des noms de commandes vers les handlers {@link ICommand}
   */
  public Map<String, ICommand> getCommands() {
    return commands;
  }
}
