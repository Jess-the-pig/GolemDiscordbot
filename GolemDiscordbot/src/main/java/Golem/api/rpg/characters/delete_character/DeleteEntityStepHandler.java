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

@RequiredArgsConstructor
public class DeleteEntityStepHandler<T extends TimeStampedEntity>
    implements StepHandler<T, ContentCarrier> {

  private final Function<String, T> entityFinder;
  private final Consumer<T> deleteAction;

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
