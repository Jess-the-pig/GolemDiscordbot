package Golem.api.rpg.characters;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.utils.Session;
import Golem.api.db.CharacterRepository;
import Golem.api.rpg.dto.ReplyFactory;
import java.util.Map;
import java.util.function.BiConsumer;
import reactor.core.publisher.Mono;

public class ChooseFieldStepHandler implements StepHandler<Characters, ContentCarrier> {

  private final CharacterRepository characterRepository;
  private final Map<String, BiConsumer<Characters, String>> stringFieldSetters;

  public ChooseFieldStepHandler(
      CharacterRepository repo, Map<String, BiConsumer<Characters, String>> setters) {
    this.characterRepository = repo;
    this.stringFieldSetters = setters;
  }

  @Override
  public Mono<Void> handle(ContentCarrier event, Session<Characters> session) {
    String content = event.getContent().trim();

    if ("done".equalsIgnoreCase(content)) {
      characterRepository.save(session.entity);
      return ReplyFactory.reply(event.getDelegate(), "All done! Character saved.");
    }

    String field = content.toLowerCase();

    if (!stringFieldSetters.containsKey(field)
        && !"level".equals(field)
        && !"experiencepoints".equals(field)) {
      return ReplyFactory.reply(event.getDelegate(), "Unknown field. Try again.");
    }

    session.lastField = field;
    session.step = 2;
    return ReplyFactory.reply(event.getDelegate(), "What is the new value for **" + field + "** ?");
  }
}
