package Golem.api.rpg.characters.modify_character;

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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CharacterModifyService {

  private final CharacterRepository characterRepository;

  // La session
  private final Map<Long, Session<Characters>> modificationSessions = new HashMap<>();

  // Déclaration de la map avant l'utilisation
  private static final Map<String, BiConsumer<Characters, String>> STRING_FIELD_SETTERS =
      Map.of(
          "name", Characters::setCharacterName,
          "race", Characters::setRace,
          "class", Characters::setClass_,
          "background", Characters::setBackground,
          "featuresandtraits", Characters::setFeaturesAndTraits,
          "languages", Characters::setLanguages,
          "personalitytraits", Characters::setPersonalityTraits);

  private final List<StepHandler<Characters, ContentCarrier>> modificationSteps;

  public CharacterModifyService(CharacterRepository characterRepository) {
    this.characterRepository = characterRepository;
    this.modificationSteps =
        List.of(
            new SelectCharacterStepHandler(characterRepository),
            new ChooseFieldStepHandler(characterRepository, STRING_FIELD_SETTERS),
            new UpdateFieldStepHandler(characterRepository, STRING_FIELD_SETTERS));
  }

  public Mono<Void> handleModify(ButtonInteractionEvent event) {
    long userId = event.getInteraction().getUser().getId().asLong();
    String username = event.getInteraction().getUser().getUsername();

    Session<Characters> session = new Session<>();
    session.step = 0;
    session.entity = null; // Pas encore choisi
    modificationSessions.put(userId, session);

    List<Characters> allPlayerCharacters = characterRepository.findByPlayerName(username);
    if (allPlayerCharacters.isEmpty()) {
      return ReplyFactory.deferAndSend(event, "You don't have any characters to modify.");
    }

    StringBuilder charactersList = new StringBuilder();
    for (Characters c : allPlayerCharacters) {
      charactersList.append("- ").append(c.getCharacterName()).append("\n");
    }

    return ReplyFactory.deferAndSend(
        event,
        "Let's modify your character!\nHere are your characters:\n"
            + charactersList
            + "\nWhich one do you want to modify?");
  }

  public List<DiscordEventHandler<?>> getEventHandlers() {
    return List.of(new DiscordEventHandler<>(MessageCreateEvent.class, this::handleMessageModify));
  }

  public Mono<Void> handleMessageModify(MessageCreateEvent event) {
    long userId = event.getMessage().getAuthor().map(u -> u.getId().asLong()).orElse(-1L);
    if (userId == -1) return Mono.empty();

    Session<Characters> session = modificationSessions.get(userId);
    if (session == null) return Mono.empty();

    if (session.step >= modificationSteps.size()) {
      modificationSessions.remove(userId);
      return ReplyFactory.reply(event, "All done!");
    }

    StepHandler<Characters, ContentCarrier> handler = modificationSteps.get(session.step);

    // On crée un ContentCarrier à partir de MessageCreateEvent,
    // ici je suppose que tu as une classe wrapper adaptée
    ContentCarrier carrier = new MessageCreateEventWrapper(event);

    return handler.handle(carrier, session);
  }

  private Mono<Void> applyFieldUpdate(
      MessageCreateEvent event, Session<Characters> session, String newValue) {
    String field = session.lastField;

    if (STRING_FIELD_SETTERS.containsKey(field)) {
      STRING_FIELD_SETTERS.get(field).accept(session.entity, newValue);
    } else if ("level".equals(field)) {
      try {
        session.entity.setLevel(Integer.parseInt(newValue));
      } catch (NumberFormatException e) {
        return Mono.error(
            new IllegalArgumentException("Please enter a valid integer for the level."));
      }
    } else if ("experiencepoints".equals(field)) {
      try {
        session.entity.setExperiencePoints(Integer.parseInt(newValue));
      } catch (NumberFormatException e) {
        return Mono.error(
            new IllegalArgumentException("Please enter a valid integer for experience points."));
      }
    } else {
      return Mono.error(new IllegalArgumentException("I don't know this field. Try again."));
    }

    session.entity.setLastUpdated(LocalDateTime.now());
    characterRepository.save(session.entity);
    return ReplyFactory.reply(event, "Updated! Anything else? Or type **done**.");
  }
}
