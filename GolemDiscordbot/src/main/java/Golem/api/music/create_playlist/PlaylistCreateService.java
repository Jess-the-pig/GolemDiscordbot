package Golem.api.music.create_playlist;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.utils.Session;
import Golem.api.common.wrappers.MessageCreateEventWrapper;
import Golem.api.db.PlaylistRepository;
import Golem.api.music.Playlist;
import Golem.api.rpg.dto.ReplyFactory;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service pour créer des playlists sur Discord.
 *
 * <p>Gère la session de création par utilisateur et utilise des {@link StepHandler} pour gérer
 * l'ajout de tracks.
 */
@Service
public class PlaylistCreateService {

  private final PlaylistRepository playlistRepository;
  private final Map<Long, Session<Playlist>> modificationSessions = new HashMap<>();

  private final List<StepHandler<Playlist, ContentCarrier>> modificationSteps;

  /**
   * Crée un service de création de playlists.
   *
   * @param playlistRepository le repository pour sauvegarder les playlists
   */
  public PlaylistCreateService(PlaylistRepository playlistRepository) {
    this.playlistRepository = playlistRepository;

    // Une seule étape : ajouter des tracks
    this.modificationSteps =
        List.of(
            new AddTrackStepHandler<>(
                playlistRepository::save, "Added! Send another URL or type **done** to finish."));
  }

  /**
   * Démarre la création d'une playlist pour un utilisateur via un bouton Discord.
   *
   * @param event l'événement d'interaction du bouton
   * @return un {@link Mono} indiquant la complétion du traitement
   */
  public Mono<Void> handleCreate(ButtonInteractionEvent event) {
    long userId = event.getInteraction().getUser().getId().asLong();

    Playlist newPlaylist = new Playlist();
    newPlaylist.setUserPlaylist(userId);

    Session<Playlist> session = new Session<>();
    session.step = 0;
    session.entity = newPlaylist;

    modificationSessions.put(userId, session);

    return ReplyFactory.deferAndSend(
        event, "Let's create a playlist. Send track URLs one by one. Type **done** when finished.");
  }

  /**
   * Gère les messages envoyés par l'utilisateur pour ajouter des tracks à sa playlist.
   *
   * @param event l'événement de création de message Discord
   * @return un {@link Mono} indiquant la complétion du traitement
   */
  public Mono<Void> handleMessageCreate(MessageCreateEvent event) {
    long userId = event.getMessage().getAuthor().map(u -> u.getId().asLong()).orElse(-1L);
    if (userId == -1) return Mono.empty();

    Session<Playlist> session = modificationSessions.get(userId);
    if (session == null) return Mono.empty();

    StepHandler<Playlist, ContentCarrier> handler = modificationSteps.get(0);
    ContentCarrier carrier = new MessageCreateEventWrapper(event);

    return handler
        .handle(carrier, session)
        .doOnTerminate(
            () -> {
              if ("done".equalsIgnoreCase(event.getMessage().getContent())) {
                modificationSessions.remove(userId);
              }
            });
  }
}
