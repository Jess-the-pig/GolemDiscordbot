package Golem.api.rpg.campaign;

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
public class campaignCommand implements ICommand, HasButtons {

  private final CampaignService campaignService;

  @Override
  public Mono<Void> handleButtonInteraction(ButtonInteractionEvent event) {
    String customId = event.getCustomId();
    switch (customId) {
      case "character:create":
        return campaignService.handleCreate(event);
      default:
        return Mono.empty();
    }
  }

  @Override
  public String getName() {
    return "campaign";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return event
        .reply("Que veux-tu faire ?")
        .withComponents(
            discord4j.core.object.component.ActionRow.of(
                Button.primary("character:create", "Create a campaign")))
        .then();
  }
}
