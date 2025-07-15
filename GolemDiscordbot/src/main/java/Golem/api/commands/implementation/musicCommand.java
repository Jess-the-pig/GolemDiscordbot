package Golem.api.commands.implementation;

import Golem.api.commands.ICommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

public class musicCommand implements ICommand {
  private RestTemplate restTemplate;

  public musicCommand(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public String getName() {
    return "music";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'handle'");
  }

  public void soundConnect() {}
}
