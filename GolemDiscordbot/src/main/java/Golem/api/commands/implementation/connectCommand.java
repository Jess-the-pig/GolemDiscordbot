package Golem.api.commands.implementation;

import Golem.api.commands.ICommand;
import Golem.api.utils.QueuedAudioProvider;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class connectCommand implements ICommand {
  private final QueuedAudioProvider pQueuedAudioProvider;

  public connectCommand(QueuedAudioProvider pQueuedAudioProvider) {
    this.pQueuedAudioProvider = pQueuedAudioProvider;
  }

  @Override
  public String getName() {
    return "connect";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return Mono.justOrEmpty(event.getInteraction().getMember())
        .flatMap(Member::getVoiceState)
        .flatMap(VoiceState::getChannel)
        .ofType(VoiceChannel.class)
        .flatMap(
            channel -> {
              if (pQueuedAudioProvider.isConnected()) {
                return event.reply("✅ Déjà connecté !");
              }
              return channel
                  .join(spec -> spec.setProvider(pQueuedAudioProvider).setSelfDeaf(true))
                  .doOnSuccess(voiceConnection -> pQueuedAudioProvider.setConnected(true))
                  .then(event.reply("✅ Connecté au vocal !"));
            })
        .switchIfEmpty(event.reply("❌ Tu dois être dans un salon vocal pour me connecter."));
  }
}
