package Golem.api.rpg.characters.modify_character;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.interfaces.TimeStampedEntity;
import Golem.api.common.utils.Session;
import Golem.api.rpg.dto.ReplyFactory;

import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Étape de modification d'un personnage permettant à l'utilisateur de choisir quel champ il
 * souhaite modifier.
 *
 * <p>Le handler attend que l'utilisateur envoie le nom du champ à modifier. Si le champ existe dans
 * la map {@code fieldSetters}, la session passe à l'étape suivante pour fournir la nouvelle valeur.
 *
 * @param <T> type de l'entité modifiable, doit implémenter TimeStampedEntity
 */
@RequiredArgsConstructor
public class ChooseFieldStepHandler<T extends TimeStampedEntity>
        implements StepHandler<T, ContentCarrier> {

    private final Function<String, T> entityFinder;
    private final Map<String, BiConsumer<T, Object>> fieldSetters;

    @Override
    public Mono<Void> handle(ContentCarrier event, Session<T> session) {
        String content = event.getContent().trim();

        if ("done".equalsIgnoreCase(content)) {
            entityFinder.apply("").setLastUpdated(LocalDateTime.now());
            // Persiste : à ajuster selon ton pattern repo
            return ReplyFactory.reply(event.getDelegate(), "All done! Entity saved.");
        }

        String field = content.toLowerCase();

        if (!fieldSetters.containsKey(field)) {
            return ReplyFactory.reply(event.getDelegate(), "Unknown field. Try again.");
        }

        session.lastField = field;
        session.step = 2;
        return ReplyFactory.reply(
                event.getDelegate(), "What is the new value for **" + field + "** ?");
    }
}
