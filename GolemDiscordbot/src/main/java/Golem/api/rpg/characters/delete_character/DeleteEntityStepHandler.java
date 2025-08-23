package Golem.api.rpg.characters.delete_character;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.interfaces.TimeStampedEntity;
import Golem.api.common.utils.Session;
import Golem.api.rpg.dto.ReplyFactory;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Étape générique pour supprimer une entité dans une session interactive.
 *
 * <p>Cette classe est utilisée dans les services de suppression de personnages ou de playlists.
 * Elle : - Cherche l'entité à supprimer à partir d'un nom fourni par l'utilisateur - Supprime
 * l'entité via le Consumer fourni - Fournit un retour à l'utilisateur via Discord
 *
 * @param <T> type d'entité à supprimer, doit implémenter TimeStampedEntity
 */
@RequiredArgsConstructor
public class DeleteEntityStepHandler<T extends TimeStampedEntity>
    implements StepHandler<T, ContentCarrier> {

  private final Function<String, T> entityFinder;
  private final Consumer<T> deleteAction;

  /**
   * Gère la suppression de l'entité à partir de l'entrée utilisateur.
   *
   * <p>Si l'utilisateur tape "done", la session se termine. Sinon : - l'entité est recherchée - si
   * elle existe, elle est supprimée - l'utilisateur reçoit un message de confirmation
   *
   * @param event événement contenant le contenu utilisateur
   * @param session session en cours
   * @return Mono<Void> représentant le traitement asynchrone
   */
  @Override
  public Mono<Void> handle(ContentCarrier event, Session<T> session) {
    String content = event.getContent().trim();

    if ("done".equalsIgnoreCase(content)) {
      return ReplyFactory.reply(event.getDelegate(), "All done! Deletion process finished.");
    }

    T entityToDelete = entityFinder.apply(content);

    if (entityToDelete == null) {
      return ReplyFactory.reply(event.getDelegate(), "No entity found with that name. Try again.");
    }

    deleteAction.accept(entityToDelete);

    return ReplyFactory.reply(
        event.getDelegate(),
        "Deleted **"
            + content
            + "**. Want to delete another? Type the name or **done** to finish.");
  }
}
