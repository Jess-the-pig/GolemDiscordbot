package Golem.api.common.interfaces;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import java.util.List;
import reactor.core.publisher.Mono;

/** Interface pour les objets qui peuvent gérer des interactions de boutons. */
public interface HasButtons {

  /** Traite une interaction de bouton. */
  Mono<Void> handleButtonInteraction(ButtonInteractionEvent event);

  /** Retourne la liste des customId gérés par cette commande */
  List<String> getCustomIds();
}
