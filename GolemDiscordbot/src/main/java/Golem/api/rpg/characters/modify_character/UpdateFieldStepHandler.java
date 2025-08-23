package Golem.api.rpg.characters.modify_character;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.interfaces.TimeStampedEntity;
import Golem.api.common.utils.Session;
import Golem.api.rpg.dto.ReplyFactory;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Étape de modification permettant de mettre à jour un champ spécifique d'une entité sélectionnée.
 *
 * <p>Cette étape récupère la valeur saisie par l'utilisateur et applique le setter correspondant
 * depuis {@code fieldSetters}. Après la mise à jour, l'entité est enregistrée via {@code
 * saveAction}.
 *
 * @param <T> type de l'entité modifiable, doit implémenter TimeStampedEntity
 */
@RequiredArgsConstructor
public class UpdateFieldStepHandler<T extends TimeStampedEntity>
    implements StepHandler<T, ContentCarrier> {

  private final Consumer<T> saveAction;
  private final Map<String, BiConsumer<T, Object>> fieldSetters;

  /**
   * Gère la saisie utilisateur pour mettre à jour un champ de l'entité.
   *
   * <p>Si le champ est inconnu, renvoie un message d'erreur. Sinon, applique la valeur, met à jour
   * la date de dernière modification, sauvegarde l'entité, et permet à l'utilisateur de modifier
   * d'autres champs.
   *
   * @param event événement contenant le contenu utilisateur
   * @param session session courante de l'utilisateur
   * @return Mono<Void> représentant le traitement asynchrone
   */
  @Override
  public Mono<Void> handle(ContentCarrier event, Session<T> session) {
    String newValue = event.getContent().trim();
    String field = session.lastField;

    try {
      if (fieldSetters.containsKey(field)) {
        // Appliquer le setter universel
        fieldSetters.get(field).accept(session.entity, newValue);
      } else {
        return ReplyFactory.reply(event.getDelegate(), "Unknown field. Try again.");
      }
    } catch (NumberFormatException e) {
      return ReplyFactory.reply(
          event.getDelegate(), "Invalid number format for field **" + field + "**");
    }

    session.entity.setLastUpdated(LocalDateTime.now());
    saveAction.accept(session.entity);
    session.step = 1;
    return ReplyFactory.reply(event.getDelegate(), "Updated! Anything else? Or type **done**.");
  }
}
