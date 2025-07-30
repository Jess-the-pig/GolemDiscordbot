package Golem.api.discordgetaway.getresponse;

import Golem.api.common.commands.ICommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.AudioProvider;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class joinCommand implements ICommand {

  private final AudioProvider provider;

  public joinCommand(AudioProvider provider) {
    this.provider = provider;
  }

  @Override
  public String getName() {
    return "join";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return Mono.justOrEmpty(event.getInteraction().getMember())
        .flatMap(Member::getVoiceState)
        .flatMap(VoiceState::getChannel)
        .ofType(VoiceChannel.class)
        .flatMap(
            channel ->
                channel.join(
                    spec -> {
                      spec.setProvider(provider);
                      spec.setSelfDeaf(true); // 👈 Ajoute ça !
                    }))
        .then();
  }
}
