package Golem.api.music.create_playlist;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.interfaces.TimeStampedEntity;
import Golem.api.common.utils.Session;
import Golem.api.music.Playlist;
import Golem.api.rpg.dto.ReplyFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * StepHandler pour ajouter des tracks à une {@link Playlist}.
 *
 * <p>Permet à l'utilisateur d'ajouter plusieurs URL de tracks successivement et de terminer la
 * saisie avec le mot "done". Met à jour l'entité et sauvegarde à chaque ajout.
 *
 * @param <T> le type d'entité étendant {@link TimeStampedEntity}
 */
@RequiredArgsConstructor
public class AddTrackStepHandler<T extends TimeStampedEntity>
    implements StepHandler<T, ContentCarrier> {

  private final Consumer<T> saveAction;
  private final String prompt; // message à afficher après chaque ajout

  /**
   * Gère l'ajout d'un track à la playlist ou termine la saisie si l'utilisateur tape "done".
   *
   * @param event le carrier contenant le contenu saisi par l'utilisateur
   * @param session la session contenant l'entité et l'état courant
   * @return un {@link Mono} indiquant la complétion du traitement
   */
  @Override
  public Mono<Void> handle(ContentCarrier event, Session<T> session) {
    String newValue = event.getContent().trim();

    if ("done".equalsIgnoreCase(newValue)) {
      // Fin de la saisie
      session.step += 1; // passe à l'étape suivante
      if (session.entity != null) {
        session.entity.setLastUpdated(LocalDateTime.now());
        saveAction.accept(session.entity);
      }
      return ReplyFactory.reply(event.getDelegate(), "All tracks added! Playlist saved.");
    }

    try {
      if (session.entity instanceof Playlist playlist) {
        if (playlist.getUrl() == null) {
          playlist.setUrl(new ArrayList<>());
        }
        playlist.getUrl().add(newValue);
        // Sauvegarde optionnelle à chaque ajout
        session.entity.setLastUpdated(LocalDateTime.now());
        saveAction.accept(session.entity);
      } else {
        return ReplyFactory.reply(event.getDelegate(), "Cannot add track: invalid entity.");
      }
    } catch (Exception e) {
      return ReplyFactory.reply(event.getDelegate(), "Error adding track. Try again.");
    }

    return ReplyFactory.reply(event.getDelegate(), prompt);
  }
}
