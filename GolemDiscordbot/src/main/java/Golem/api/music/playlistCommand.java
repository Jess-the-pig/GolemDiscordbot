package Golem.api.music;

import Golem.api.common.interfaces.HasButtons;
import Golem.api.common.interfaces.ICommand;
import Golem.api.music.create_playlist.PlaylistCreateService;
import Golem.api.music.delete_playlist.PlaylistDeleteService;
import Golem.api.music.play_playlist.PlayPlaylistService;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Commande Discord permettant de gérer les playlists utilisateur.
 *
 * <p>Cette commande est enregistrée sous le nom {@code /playlist} et affiche un menu interactif
 * avec plusieurs boutons :
 *
 * <p>Créer : démarre un flux pour créer une nouvelle playlist via {@link PlaylistCreateService}.
 * Supprimer : permet de sélectionner et supprimer une playlist existante via {@link
 * PlaylistDeleteService}. Lancer : lit une playlist enregistrée via {@link PlayPlaylistService}.
 *
 * <p>La logique de traitement des boutons est gérée par l’interface {@link HasButtons}.
 */
@Component
@RequiredArgsConstructor
public class playlistCommand implements ICommand, HasButtons {

  private final PlaylistCreateService playlistCreateService;
  private final PlaylistDeleteService playlistDeleteService;
  private final PlayPlaylistService playPlaylistService;

  @Override
  public String getName() {
    return "playlist";
  }

  @Override
  public List<String> getCustomIds() {
    return List.of("playlist_create", "playlist_delete", "playlist_play");
  }

  /**
   * Affiche un menu interactif sous forme de boutons pour permettre à l’utilisateur de choisir une
   * action sur ses playlists.
   *
   * @param event événement Discord lié à l’exécution de la commande
   * @return un {@link Mono} complété une fois la réponse envoyée
   */
  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    return event
        .reply("Que veux-tu faire ?")
        .withComponents(
            ActionRow.of(
                Button.primary("playlist_create", "Créer"),
                Button.secondary("playlist_delete", "Supprimer"),
                Button.success("playlist_play", "Lancer")))
        .then();
  }

  /**
   * Gère l’interaction avec un bouton cliqué par l’utilisateur.
   *
   * <p>Le mapping est basé sur l’ID du bouton défini dans {@link #handle} :
   *
   * <p>{@code Create} → {@link PlaylistCreateService#handleCreate} {@code Delete} → {@link
   * PlaylistDeleteService#handleMessageDelete} {@code Play} → {@link
   * PlayPlaylistService#playPlaylist}
   *
   * @param event événement Discord lié à un clic sur bouton
   * @return un {@link Mono} complété après traitement
   */
  @Override
  public Mono<Void> handleButtonInteraction(ButtonInteractionEvent event) {
    String customId = event.getCustomId();

    switch (customId) {
      case "create":
        return playlistCreateService.handleCreate(event);
      case "delete":
        return playlistDeleteService.handleMessageDelete(event);
      case "play":
        return playPlaylistService.playPlaylist(event);
      default:
        return Mono.empty();
    }
  }
}
