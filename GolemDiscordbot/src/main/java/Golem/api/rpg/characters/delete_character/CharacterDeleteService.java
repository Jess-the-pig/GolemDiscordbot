package Golem.api.rpg.characters.delete_character;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.utils.Session;
import Golem.api.common.wrappers.MessageCreateEventWrapper;
import Golem.api.db.CharacterRepository;
import Golem.api.discordgetaway.DiscordEventHandler;
import Golem.api.rpg.characters.Characters;
import Golem.api.rpg.dto.ReplyFactory;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CharacterDeleteService {
  private final CharacterRepository characterRepository;
  private final Map<Long, Session<Characters>> deleteSessions = new HashMap<>();
  private final List<StepHandler<Characters, ContentCarrier>> deletionSteps;

  public List<DiscordEventHandler<?>> getEventHandlers() {
    return List.of(
        new DiscordEventHandler<>(ButtonInteractionEvent.class, this::handleMessageDelete));
  }

  public Mono<Void> handleMessageDelete(ButtonInteractionEvent event) {
    long userId = event.getInteraction().getUser().getId().asLong();
    String username = event.getInteraction().getUser().getUsername();

    Session<Characters> session = new Session<>();
    session.step = 0;
    session.entity = null; // Pas encore choisi
    deleteSessions.put(userId, session);

    List<Characters> allPlayerCharacters = characterRepository.findByPlayerName(username);
    if (allPlayerCharacters.isEmpty()) {
      return ReplyFactory.deferAndSend(event, "You don't have any characters to delete.");
    }

    StringBuilder charactersList = new StringBuilder();
    for (Characters c : allPlayerCharacters) {
      charactersList.append("- ").append(c.getCharacterName()).append("\n");
    }

    return ReplyFactory.deferAndSend(
        event,
        "Let's delete your character!\nHere are your characters:\n"
            + charactersList
            + "\nWhich one do you want to delete?");
  }

  public CharacterDeleteService(CharacterRepository characterRepository) {
    this.characterRepository = characterRepository;
    this.deletionSteps =
        List.of(
            new DeleteEntityStepHandler<>(
                name -> characterRepository.findByCharacterName(name),
                characterRepository::delete));
  }

  public Mono<Void> handleMessageModify(MessageCreateEvent event) {
    long userId = event.getMessage().getAuthor().map(u -> u.getId().asLong()).orElse(-1L);
    if (userId == -1) return Mono.empty();

    Session<Characters> session = deleteSessions.get(userId);
    if (session == null) return Mono.empty();

    if (session.step >= deletionSteps.size()) {
      deleteSessions.remove(userId);
      return ReplyFactory.reply(event, "All done!");
    }

    StepHandler<Characters, ContentCarrier> handler = deletionSteps.get(session.step);

    // On crée un ContentCarrier à partir de MessageCreateEvent,
    // ici je suppose que tu as une classe wrapper adaptée
    ContentCarrier carrier = new MessageCreateEventWrapper(event);

    return handler.handle(carrier, session);
  }
}
