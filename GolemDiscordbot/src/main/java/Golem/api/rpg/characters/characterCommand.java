package Golem.api.rpg.characters;

import Golem.api.common.interfaces.HasButtons;
import Golem.api.common.interfaces.ICommand;
import Golem.api.rpg.characters.consult_character.CharacterConsultService;
import Golem.api.rpg.characters.create_character.CharacterCreateService;
import Golem.api.rpg.characters.modify_character.CharacterModifyService;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.Button;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class characterCommand implements ICommand, HasButtons {

  private final CharacterConsultService characterConsultService;
  private final CharacterCreateService characterCreateService;
  private final CharacterModifyService characterModifyService;

  @Override
  public String getName() {
    return "character";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return event
        .reply("Que veux-tu faire ?")
        .withComponents(
            discord4j.core.object.component.ActionRow.of(
                Button.primary("character:create", "Créer"),
                Button.secondary("character:modify", "Modifier"),
                Button.success("character:consult", "Consulter")))
        .then();
  }

  @Override
  public Mono<Void> handleButtonInteraction(ButtonInteractionEvent event) {
    String customId = event.getCustomId();

    switch (customId) {
      case "character:create":
        return characterCreateService.handleMessageCreate(event);
      case "character:modify":
        return characterModifyService.handleModify(event);
      case "character:consult":
        return characterConsultService.handleConsult(event);
      default:
        return Mono.empty();
    }
  }
}
