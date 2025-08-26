package Golem.api.rpg.npcs;

import Golem.api.common.interfaces.HasButtons;
import Golem.api.common.interfaces.ICommand;
import Golem.api.rpg.npcs.consult_npcs.NpcConsultService;
import Golem.api.rpg.npcs.create_npcs.NpcCreateService;
import Golem.api.rpg.npcs.delete_npcs.DeleteNpcsService;
import Golem.api.rpg.npcs.modify_npcs.NpcsModifyService;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class npcsCommand implements ICommand, HasButtons {
    private final NpcConsultService npcConsultService;
    private final NpcCreateService npcCreateService;
    private final DeleteNpcsService deleteNpcsService;
    private final NpcsModifyService npcsModifyService;

    @Override
    public String getName() {
        return "npc";
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
        return event.reply("Que veux-tu faire ?")
                .withComponents(
                        ActionRow.of(
                                Button.primary("npc_create", "Créer"),
                                Button.secondary("npc_modify", "Modifier"),
                                Button.success("npc_consult", "Consulter"),
                                Button.danger("npc_delete", "Supprimer")))
                .then();
    }

    @Override
    public List<String> getCustomIds() {
        return List.of("npc_create", "npc_modify", "npc_consult", "npc_delete");
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
            case "npc_create":
                return npcCreateService.handleMessageCreate(event);
            case "npc_modify":
                return npcsModifyService.handleModify(event);
            case "npc_consult":
                return npcConsultService.handleConsult(event);
            case "npc_delete":
                return deleteNpcsService.handleMessageDelete(event);
            default:
                return Mono.empty();
        }
    }
}
