package Golem.api.rpg.characters;

import Golem.api.common.interfaces.HasButtons;
import Golem.api.common.interfaces.ICommand;
import Golem.api.rpg.characters.consult_characters.CharacterConsultService;
import Golem.api.rpg.characters.create_character.CharacterCreateService;
import Golem.api.rpg.characters.delete_character.CharacterDeleteService;
import Golem.api.rpg.characters.modify_character.CharacterModifyService;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Commande principale pour gérer les actions sur les personnages d'un joueur.
 *
 * <p>Elle permet de créer, modifier ou consulter un personnage via des boutons Discord. Chaque
 * bouton déclenche un service spécifique correspondant à l'action.
 *
 * <p>Implémente ICommand pour être utilisée comme commande Discord et HasButtons pour gérer les
 * interactions avec les boutons.
 */
@Component
@RequiredArgsConstructor
public class characterCommand implements ICommand, HasButtons {

  private final CharacterDeleteService characterDeleteService;

  private final CharacterConsultService characterConsultService;
  private final CharacterCreateService characterCreateService;
  private final CharacterModifyService characterModifyService;

  @Override
  public String getName() {
    return "character";
  }

  /**
   * Gère l'interaction de type chat (commande slash) et propose les options de création,
   * modification ou consultation via boutons.
   *
   * @param event événement d'interaction du type ChatInputInteractionEvent
   * @return Mono<Void> représentant le traitement asynchrone
   */
  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return event
        .reply("Que veux-tu faire ?")
        .withComponents(
            ActionRow.of(
                Button.primary("create", "Créer"),
                Button.secondary("modify", "Modifier"),
                Button.success("consult", "Consulter"),
                Button.danger("delete", "Supprimer")))
        .then();
  }

  @Override
  public List<String> getCustomIds() {
    return List.of("create", "modify", "consult", "delete");
  }

  /**
   * Gère l'interaction avec les boutons.
   *
   * <p>Selon le bouton cliqué, appelle le service approprié :
   *
   * <p>"character:create" → CharacterCreateService "character:modify" → CharacterModifyService
   * "character:consult" → CharacterConsultService
   *
   * @param event événement de type ButtonInteractionEvent
   * @return Mono<Void> représentant le traitement asynchrone
   */
  @Override
  public Mono<Void> handleButtonInteraction(ButtonInteractionEvent event) {
    String customId = event.getCustomId();

    switch (customId) {
      case "create":
        return characterCreateService.startCreationSession(event);
      case "modify":
        return characterModifyService.handleModify(event);
      case "consult":
        return characterConsultService.handleConsult(event);
      case "delete":
        return characterDeleteService.handleMessageDelete(event);
      default:
        return Mono.empty();
    }
  }
}
