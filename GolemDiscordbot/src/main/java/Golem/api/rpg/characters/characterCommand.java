package Golem.api.rpg.characters;

import Golem.api.common.commands.HasButtons;
import Golem.api.common.commands.ICommand;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.Button;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class characterCommand implements ICommand, HasButtons {

  private final CharacterService characterService;

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
        return characterService.handleCreate(event);
      case "character:modify":
        return characterService.handleModify(event);
      case "character:consult":
        return characterService.handleConsult(event);
      default:
        return Mono.empty();
    }
  }
}
