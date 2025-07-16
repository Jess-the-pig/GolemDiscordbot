package Golem.api.commands.implementation;

import Golem.api.commands.ICommand;
import Golem.api.services.LavaPlayerAudioProvider;
import Golem.api.services.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class playCommand implements ICommand {

  private final AudioPlayerManager playerManager;
  private final AudioPlayer player;
  private final TrackScheduler scheduler;
  private final LavaPlayerAudioProvider provider;

  public playCommand(
      AudioPlayerManager playerManager,
      AudioPlayer player,
      TrackScheduler scheduler,
      LavaPlayerAudioProvider provider) {
    this.playerManager = playerManager;
    this.player = player;
    this.scheduler = scheduler;
    this.provider = provider;
  }

  @Override
  public String getName() {
    return "play";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    String url =
        event
            .getOption("url")
            .flatMap(option -> option.getValue())
            .map(value -> value.asString())
            .orElse(null);

    if (url == null || url.isBlank()) {
      return event.reply("❌ Fournis une URL valide !").withEphemeral(true);
    }

    return Mono.justOrEmpty(event.getInteraction().getMember())
        .flatMap(Member::getVoiceState)
        .flatMap(VoiceState::getChannel)
        .ofType(VoiceChannel.class)
        .flatMap(channel -> channel.join(spec -> spec.setProvider(provider)))
        .doOnSuccess(
            ignored -> {
              playerManager.loadItemOrdered(
                  player,
                  url,
                  new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack track) {
                      log.info("✅ Track loaded: " + track.getInfo().title);

                      scheduler.queue(track);
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist playlist) {
                      for (AudioTrack track : playlist.getTracks()) {
                        scheduler.queue(track);
                      }
                    }

                    @Override
                    public void noMatches() {
                      // TODO : envoyer une réponse Discord
                    }

                    @Override
                    public void loadFailed(FriendlyException throwable) {
                      // TODO : envoyer une réponse Discord
                    }
                  });
            })
        .then(event.reply("▶️ Lecture de : " + url));
  }

  @Override
  public Optional<List<ApplicationCommandOptionData>> getOptions() {
    return Optional.of(
        List.of(
            ApplicationCommandOptionData.builder()
                .name("url")
                .description("URL de la piste à lire")
                .type(3) // 3 = STRING pour Discord
                .required(true)
                .build()));
  }
}
