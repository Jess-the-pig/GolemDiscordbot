package Golem.api.rpg.characters.modify_character;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.interfaces.TimeStampedEntity;
import Golem.api.common.utils.Session;
import Golem.api.rpg.dto.ReplyFactory;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Étape de modification permettant à l'utilisateur de sélectionner l'entité (par exemple un
 * personnage) qu'il souhaite modifier.
 *
 * <p>L'utilisateur doit fournir un identifiant ou nom d'entité. Si l'entité est trouvée via {@code
 * entityFinder}, la session passe à l'étape suivante pour la modification des champs.
 *
 * @param <T> type de l'entité modifiable, doit implémenter TimeStampedEntity
 */
@RequiredArgsConstructor
public class SelectEntityStepHandler<T extends TimeStampedEntity>
    implements StepHandler<T, ContentCarrier> {

  private final Function<String, T> entityFinder;

  /**
   * Gère la saisie utilisateur pour sélectionner une entité à modifier.
   *
   * <p>Si l'entité n'existe pas, renvoie un message d'erreur. Sinon, la session est mise à jour
   * avec l'entité sélectionnée et passe à l'étape suivante.
   *
   * @param event événement contenant le contenu utilisateur
   * @param session session courante de l'utilisateur
   * @return Mono<Void> représentant le traitement asynchrone
   */
  @Override
  public Mono<Void> handle(ContentCarrier event, Session<T> session) {
    String content = event.getContent().trim();

    T entity = entityFinder.apply(content);
    if (entity == null) {
      return ReplyFactory.reply(event.getDelegate(), "I couldn't find this entity. Try again?");
    }

    session.entity = entity;
    session.step = 1;

    return ReplyFactory.reply(
        event.getDelegate(), "What do you want to update? (name, race, class, etc.)");
  }
}
