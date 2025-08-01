package Golem.api.music.play;

import Golem.api.common.enums.DiscordOptionType;
import Golem.api.common.factories.ApplicationCommandOptionDataFactory;
import Golem.api.common.interfaces.HasOptions;
import Golem.api.common.interfaces.ICommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandOptionData;
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
public class playCommand implements ICommand, HasOptions {

  private static final Logger log = LoggerFactory.getLogger(playCommand.class);
  private final QueuedAudioProvider pQueuedAudioProvider;

  public playCommand(QueuedAudioProvider pQueuedAudioProvider) {
    this.pQueuedAudioProvider = pQueuedAudioProvider;
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
            Mono.fromRunnable(
                () -> {
                  Process ffmpegProcess = startFFmpegFromYtdlp(query);
                  InputStream pcmStream = ffmpegProcess.getInputStream();

                  pQueuedAudioProvider.queueTrack(pcmStream);
                }))
        .then(event.editReply("▶️ Ajouté à la queue !"))
        .then()
        .onErrorResume(err -> event.editReply("❌ Erreur : " + err.getMessage()).then());
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
            ApplicationCommandOptionDataFactory.option(
                DiscordOptionType.STRING, "url", "URL or search query", true)));
  }
}
