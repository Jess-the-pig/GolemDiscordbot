package Golem.api.commands.implementation;

import Golem.api.commands.ICommand;
import Golem.api.utils.FFmpegAudioProvider;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.voice.AudioProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class playCommand implements ICommand {

  private static final Logger log = LoggerFactory.getLogger(playCommand.class);

  public playCommand() {
    // Pas besoin de services LavaPlayer ici
  }

  @Override
  public String getName() {
    return "play";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    String query =
        event
            .getOption("url")
            .flatMap(opt -> opt.getValue())
            .map(val -> val.asString())
            .orElse(null);

    if (query == null || query.isBlank()) {
      return event.reply("❌ Fournis une URL ou une recherche !").withEphemeral(true).then();
    }

    return event
        .deferReply()
        .then(
            Mono.justOrEmpty(event.getInteraction().getMember())
                .flatMap(Member::getVoiceState)
                .flatMap(VoiceState::getChannel)
                .ofType(VoiceChannel.class)
                .flatMap(
                    channel -> {
                      Process ffmpegProcess = startFFmpegFromYtdlp(query);
                      if (ffmpegProcess == null) {
                        return event.editReply("❌ Impossible de lancer yt-dlp + ffmpeg").then();
                      }
                      InputStream pcmStream = ffmpegProcess.getInputStream();
                      AudioProvider provider = new FFmpegAudioProvider(pcmStream);

                      return channel
                          .join(spec -> spec.setProvider(provider).setSelfDeaf(true))
                          .then(event.editReply("▶️ Lecture lancée !"));
                    })
                .switchIfEmpty(
                    event.editReply("❌ Tu dois être dans un salon vocal pour lancer la musique")))
        .then()
        .onErrorResume(err -> event.editReply("❌ Erreur : " + err.getMessage()).then());
  }

  private Mono<Void> doJoinAndPlay(ChatInputInteractionEvent event, String query) {
    return Mono.justOrEmpty(event.getInteraction().getMember())
        .flatMap(Member::getVoiceState)
        .flatMap(VoiceState::getChannel)
        .ofType(VoiceChannel.class)
        .flatMap(
            channel -> {
              Process ffmpegProcess = startFFmpegFromYtdlp(query);
              if (ffmpegProcess == null) {
                return event.editReply("❌ Impossible de lancer yt-dlp + ffmpeg").then();
              }

              InputStream pcmStream = ffmpegProcess.getInputStream();
              AudioProvider provider = new FFmpegAudioProvider(pcmStream);

              return channel
                  .join(spec -> spec.setProvider(provider).setSelfDeaf(true))
                  .then(event.editReply("▶️ Lecture lancée !").then());
            })
        .switchIfEmpty(
            event.editReply("❌ Tu dois être dans un salon vocal pour lancer la musique").then());
  }

  /**
   * Lance yt-dlp en pipe pour récupérer le meilleur audio, puis passe la sortie vers ffmpeg qui
   * convertit le flux en PCM 16 bits 48kHz stéréo.
   */
  private Process startFFmpegFromYtdlp(String query) {
    try {
      // Démarre yt-dlp pour streamer l'audio
      ProcessBuilder ytdlpPb = new ProcessBuilder("yt-dlp", "-f", "bestaudio", "-o", "-", query);
      Process ytdlpProcess = ytdlpPb.start();

      // Démarre ffmpeg pour lire depuis stdin et convertir en PCM
      ProcessBuilder ffmpegPb =
          new ProcessBuilder(
              "ffmpeg", "-i", "pipe:0", "-f", "s16le", "-ar", "48000", "-ac", "2", "pipe:1");
      ffmpegPb.redirectError(ProcessBuilder.Redirect.INHERIT);
      Process ffmpegProcess = ffmpegPb.start();

      // Transfert le flux stdout de yt-dlp vers stdin de ffmpeg dans un thread séparé
      InputStream ytdlpOut = ytdlpProcess.getInputStream();
      OutputStream ffmpegIn = ffmpegProcess.getOutputStream();

      new Thread(
              () -> {
                try (ytdlpOut;
                    ffmpegIn) {
                  ytdlpOut.transferTo(ffmpegIn);
                } catch (IOException e) {
                  log.error("Erreur lors du transfert yt-dlp -> ffmpeg", e);
                }
              })
          .start();

      return ffmpegProcess;
    } catch (IOException e) {
      log.error("Erreur lancement yt-dlp + ffmpeg", e);
      return null;
    }
  }

  @Override
  public Optional<List<ApplicationCommandOptionData>> getOptions() {
    return Optional.of(
        List.of(
            ApplicationCommandOptionData.builder()
                .name("url")
                .description("URL ou mots-clés de recherche")
                .type(3)
                .required(true)
                .build()));
  }
}
