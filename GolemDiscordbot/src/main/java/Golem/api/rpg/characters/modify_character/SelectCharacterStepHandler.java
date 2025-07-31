package Golem.api.rpg.characters;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.utils.Session;
import Golem.api.db.CharacterRepository;
import Golem.api.rpg.dto.ReplyFactory;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SelectCharacterStepHandler implements StepHandler<Characters, ContentCarrier> {

  private final CharacterRepository characterRepository;

  @Override
  public Mono<Void> handle(ContentCarrier event, Session<Characters> session) {
    String content = event.getContent().trim();

    Characters charToModify = characterRepository.findByCharacterName(content);
    if (charToModify == null) {
      return ReplyFactory.reply(event.getDelegate(), "I couldn't find this character. Try again?");
    }
    session.entity = charToModify;
    session.step = 1;
    return ReplyFactory.reply(
        event.getDelegate(), "What do you want to update? (name, race, class, etc.)");
  }
}
