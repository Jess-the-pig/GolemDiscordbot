package Golem.api.rpg.monsters;

import Golem.api.common.interfaces.HasButtons;
import Golem.api.common.interfaces.ICommand;
import Golem.api.rpg.monsters.consult_monsters.MonsterConsultService;
import Golem.api.rpg.monsters.create_monster.MonsterCreateService;
import Golem.api.rpg.monsters.delete_monster.MonsterDeleteService;
import Golem.api.rpg.monsters.modify_monster.MonsterModifyService;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Component
public class monstersCommand implements ICommand, HasButtons {
    private final MonsterConsultService monsterConsultService;
    private final MonsterCreateService monsterCreateService;
    private final MonsterModifyService monsterModifyService;
    private final MonsterDeleteService monsterDeleteService;

    @Override
    public String getName() {
        return "monster";
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
                                Button.primary("monster_create", "Créer"),
                                Button.secondary("monster_modify", "Modifier"),
                                Button.success("monster_consult", "Consulter"),
                                Button.danger("monster_delete", "Supprimer")))
                .then();
    }

    @Override
    public List<String> getCustomIds() {
        return List.of("monster_create", "monster_modify", "monster_consult", "monster_delete");
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
            case "monster_create":
                return monsterCreateService.handleMessageCreate(event);
            case "monster_modify":
                return monsterModifyService.handleModify(event);
            case "monster_consult":
                return monsterConsultService.handleConsult(event);
            case "monster_delete":
                return monsterDeleteService.handleMessageDelete(event);
            default:
                return Mono.empty();
        }
    }
}
