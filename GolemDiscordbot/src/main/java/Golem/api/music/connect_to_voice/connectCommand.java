package Golem.api.music.connect_to_voice;

import Golem.api.common.interfaces.ICommand;
import Golem.api.music.play_song.QueuedAudioProvider;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Commande Discord permettant au bot de se connecter à un canal vocal.
 *
 * <p>Vérifie si l'utilisateur est dans un salon vocal et si le bot n'est pas déjà connecté.
 */
@Component
public class connectCommand implements ICommand {
  private final QueuedAudioProvider pQueuedAudioProvider;

  /**
   * Crée une commande "connect" avec le fournisseur audio fourni.
   *
   * @param pQueuedAudioProvider le fournisseur audio gérant la file d'attente
   */
  public connectCommand(QueuedAudioProvider pQueuedAudioProvider) {
    this.pQueuedAudioProvider = pQueuedAudioProvider;
  }

  @Override
  public String getName() {
    return "connect";
  }

  /**
   * Gère l'exécution de la commande.
   *
   * <p>Le bot rejoint le salon vocal de l'utilisateur si possible. Répond avec un message d'erreur
   * si l'utilisateur n'est pas dans un canal vocal ou si le bot est déjà connecté.
   *
   * @param event l'événement d'interaction de commande
   * @return un {@link Mono} indiquant la complétion du traitement
   */
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
