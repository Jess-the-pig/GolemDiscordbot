package Golem.api.rpg.campaign;

import Golem.api.common.interfaces.HasButtons;
import Golem.api.common.interfaces.ICommand;
import Golem.api.rpg.campaign.add_npc.AddNpcToCampaignService;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.Button;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Commande Discord permettant de gérer les campagnes RPG.
 *
 * <p>Cette commande propose un menu interactif avec des boutons permettant de créer une campagne ou
 * d'ajouter un NPC à une campagne.
 *
 * <p>Elle implémente les interfaces ICommand (commande slash) et HasButtons (gestion des
 * interactions avec les boutons Discord).
 */
@Component
@RequiredArgsConstructor
public class campaignCommand implements ICommand, HasButtons {

  private final CampaignService campaignService;
  private final AddNpcToCampaignService addNpcToCampaignService;

  /**
   * Gère les interactions avec les boutons Discord liés à la commande "campaign".
   *
   * @param event événement d'interaction bouton
   * @return un Mono complété une fois la réponse envoyée
   */
  @Override
  public Mono<Void> handleButtonInteraction(ButtonInteractionEvent event) {
    String customId = event.getCustomId();
    switch (customId) {
      case "character:create":
        return campaignService.handleCreate(event);
      case "npc:add": // 👈 on route vers CampaignService
        return addNpcToCampaignService.handleAdd(event);
      default:
        return Mono.empty();
    }
  }

  @Override
  public String getName() {
    return "campaign";
  }

  /**
   * Gère l'exécution de la commande slash "campaign". Affiche un message avec deux boutons :
   * création de campagne et ajout de NPC.
   *
   * @param event événement de commande slash
   * @return un Mono complété une fois la réponse envoyée
   */
  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return event
        .reply("Que veux-tu faire ?")
        .withComponents(
            discord4j.core.object.component.ActionRow.of(
                Button.primary("character:create", "Create a campaign"),
                Button.primary("npc:add", "Add NPC to campaign") // 👈 nouveau bouton
                ))
        .then();
  }
}
