package Golem.api.music.play_playlist;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.utils.Session;
import Golem.api.db.PlaylistRepository;
import Golem.api.discordgetaway.DiscordEventHandler;
import Golem.api.music.Playlist;
import Golem.api.music.play_song.QueuedAudioProvider;
import Golem.api.rpg.dto.ReplyFactory;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service pour jouer des playlists sur Discord.
 *
 * <p>Gère la sélection et la mise en queue des tracks pour l'utilisateur, en utilisant {@link
 * QueuedAudioProvider} pour la lecture audio.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlayPlaylistService {

  private final PlaylistRepository playlistRepository;
  private final QueuedAudioProvider queuedAudioProvider;

  private final Map<Long, Session<Playlist>> playSessions = new HashMap<>();
  private final List<StepHandler<Playlist, ContentCarrier>> selectionSteps;

  /**
   * Démarre la lecture d'une playlist pour un utilisateur via un bouton Discord.
   *
   * @param event l'événement d'interaction du bouton
   * @return un {@link Mono} indiquant la complétion du traitement
   */
  public Mono<Void> playPlaylist(ButtonInteractionEvent event) {
    long userId = event.getInteraction().getUser().getId().asLong();

    Session<Playlist> session = new Session<>();
    session.step = 0;
    session.entity = null;
    playSessions.put(userId, session);

    // Liste les playlists de l'utilisateur
    List<Playlist> allUserPlaylists = playlistRepository.findByUserId(userId);
    if (allUserPlaylists.isEmpty()) {
      return ReplyFactory.deferAndSend(event, "You don't have any playlists to play.");
    }

    StringBuilder playlistList = new StringBuilder();
    for (Playlist p : allUserPlaylists) {
      playlistList.append("- ").append(p.getName()).append("\n");
    }

    return ReplyFactory.deferAndSend(
        event, "Which playlist do you want to play?\nHere are your playlists:\n" + playlistList);
  }

  /**
   * Retourne les handlers d'événements Discord pour gérer la lecture de playlists.
   *
   * @return la liste des {@link DiscordEventHandler}
   */
  public List<DiscordEventHandler<?>> getEventHandlers() {
    return List.of(new DiscordEventHandler<>(MessageCreateEvent.class, this::handleMessageSelect));
  }

  /**
   * Gère les messages envoyés par l'utilisateur pour sélectionner une playlist à jouer.
   *
   * @param event l'événement de création de message Discord
   * @return un {@link Mono} indiquant la complétion du traitement
   */
  public Mono<Void> handleMessageSelect(MessageCreateEvent event) {
    long userId = event.getMessage().getAuthor().map(u -> u.getId().asLong()).orElse(-1L);
    if (userId == -1) return Mono.empty();

    Session<Playlist> session = playSessions.get(userId);
    if (session == null) return Mono.empty();

    if (session.step >= 1) { // une seule étape pour la sélection
      playSessions.remove(userId);
      return Mono.empty();
    }

    String name = event.getMessage().getContent().trim();
    Playlist playlist = playlistRepository.findByName(name);
    if (playlist == null) {
      return ReplyFactory.reply(event, "Playlist not found. Try again.");
    }

    session.entity = playlist;
    session.step = 1;

    if (playlist.getUrl() != null) {
      for (String trackUrl : playlist.getUrl()) {
        try {
          Process ffmpegProcess = startFFmpegFromYtdlp(trackUrl);
          if (ffmpegProcess != null) {
            InputStream pcmStream = ffmpegProcess.getInputStream();
            queuedAudioProvider.queueTrack(pcmStream);
          }
        } catch (Exception e) {
          log.error("Error playing track: " + trackUrl, e);
        }
      }
    }

    playSessions.remove(userId);
    return ReplyFactory.reply(event, "Playlist `" + playlist.getName() + "` queued successfully!");
  }

  /**
   * Démarre les processus yt-dlp et ffmpeg pour convertir une URL de track en flux PCM.
   *
   * @param query l'URL ou la recherche du track
   * @return le processus ffmpeg produisant le flux PCM
   */
  private Process startFFmpegFromYtdlp(String query) {
    try {
      ProcessBuilder ytdlpPb = new ProcessBuilder("yt-dlp", "-f", "bestaudio", "-o", "-", query);
      Process ytdlpProcess = ytdlpPb.start();

      ProcessBuilder ffmpegPb =
          new ProcessBuilder(
              "ffmpeg", "-i", "pipe:0", "-f", "s16le", "-ar", "48000", "-ac", "2", "pipe:1");
      ffmpegPb.redirectError(ProcessBuilder.Redirect.INHERIT);
      Process ffmpegProcess = ffmpegPb.start();

      InputStream ytdlpOut = ytdlpProcess.getInputStream();
      OutputStream ffmpegIn = ffmpegProcess.getOutputStream();

      new Thread(
              () -> {
                try (ytdlpOut;
                    ffmpegIn) {
                  ytdlpOut.transferTo(ffmpegIn);
                } catch (IOException e) {
                  log.error("Erreur transfert yt-dlp -> ffmpeg", e);
                }
              })
          .start();

      return ffmpegProcess;
    } catch (IOException e) {
      log.error("Erreur lancement yt-dlp + ffmpeg", e);
      return null;
    }
  }
}
