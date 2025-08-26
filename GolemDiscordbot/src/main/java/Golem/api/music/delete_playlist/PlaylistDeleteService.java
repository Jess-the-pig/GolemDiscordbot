package Golem.api.music.delete_playlist;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.utils.Session;
import Golem.api.common.wrappers.MessageCreateEventWrapper;
import Golem.api.db.PlaylistRepository;
import Golem.api.music.Playlist;
import Golem.api.rpg.characters.delete_character.DeleteEntityStepHandler;
import Golem.api.rpg.dto.ReplyFactory;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service pour gérer la suppression des playlists sur Discord.
 *
 * <p>Gère les sessions de suppression par utilisateur et utilise des {@link StepHandler} pour gérer
 * la suppression de playlists.
 */
@Service
public class PlaylistDeleteService {
    private final PlaylistRepository playlistRepository;
    private final Map<Long, Session<Playlist>> deleteSessions = new HashMap<>();
    private final List<StepHandler<Playlist, ContentCarrier>> deletionSteps;

    /**
     * Crée un service pour supprimer des playlists.
     *
     * @param playlistRepository le repository pour gérer les playlists
     */
    public PlaylistDeleteService(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
        this.deletionSteps =
                List.of(
                        new DeleteEntityStepHandler<>(
                                name -> playlistRepository.findByName(name),
                                playlistRepository::delete));
    }

    /**
     * Démarre la suppression d'une playlist pour un utilisateur via un bouton Discord.
     *
     * @param event l'événement d'interaction du bouton
     * @return un {@link Mono} indiquant la complétion du traitement
     */
    public Mono<Void> handleMessageDelete(ButtonInteractionEvent event) {
        long userId = event.getInteraction().getUser().getId().asLong();
        String username = event.getInteraction().getUser().getUsername();

        Session<Playlist> session = new Session<>();
        session.step = 0;
        session.entity = null; // Pas encore choisi
        deleteSessions.put(userId, session);

        List<Playlist> allPlayerPlaylist = playlistRepository.findByUserId(userId);
        if (allPlayerPlaylist.isEmpty()) {
            return ReplyFactory.deferAndSend(event, "You don't have any playlists to delete.");
        }

        StringBuilder playlistList = new StringBuilder();
        for (Playlist p : allPlayerPlaylist) {
            playlistList.append("- ").append(p.getName()).append("\n");
        }

        return ReplyFactory.deferAndSend(
                event,
                "Let's delete your playlist!\nHere are your playlists:\n"
                        + playlistList
                        + "\nWhich one do you want to delete?");
    }

    /**
     * Gère les messages envoyés par l'utilisateur pour supprimer une playlist.
     *
     * @param event l'événement de création de message Discord
     * @return un {@link Mono} indiquant la complétion du traitement
     */
    public Mono<Void> handleMessageDelete(MessageCreateEvent event) {
        long userId = event.getMessage().getAuthor().map(u -> u.getId().asLong()).orElse(-1L);
        if (userId == -1) return Mono.empty();

        Session<Playlist> session = deleteSessions.get(userId);
        if (session == null) return Mono.empty();

        if (session.step >= deletionSteps.size()) {
            deleteSessions.remove(userId);
            return ReplyFactory.reply(event, "All done!");
        }

        StepHandler<Playlist, ContentCarrier> handler = deletionSteps.get(session.step);

        // On crée un ContentCarrier à partir de MessageCreateEvent,
        // ici je suppose que tu as une classe wrapper adaptée
        ContentCarrier carrier = new MessageCreateEventWrapper(event);

        return handler.handle(carrier, session);
    }
}
