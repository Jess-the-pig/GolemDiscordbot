package Golem.api.services;

import Golem.api.entities.Characters;
import Golem.api.factories.ReplyFactory;
import Golem.api.repositories.CharacterRepository;
import Golem.api.utils.Session;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CharacterService {
  private final Map<Long, Session<Characters>> creationSessions = new HashMap<>();
  private final Map<Long, Session<Characters>> modificationSessions = new HashMap<>();
  private final Map<Long, Session<Characters>> consultSessions = new HashMap<>();

  private final CharacterRepository characterRepository;
  private static final Logger logger = LoggerFactory.getLogger(CharacterService.class);

  public Mono<Void> handleCreate(ButtonInteractionEvent event) {
    long userId = event.getInteraction().getUser().getId().asLong();
    String username = event.getInteraction().getUser().getUsername();

    logger.info("handleCreate called for userId={}, username={}", userId, username);

    Session<Characters> session = new Session<>();
    session.step = 0;
    session.entity = new Characters();
    session.entity.setPlayerName(username);
    creationSessions.put(userId, session);

    return ReplyFactory.deferAndSend(
        event, "Let's create our character! But first, What's his name ?");
  }

  public Mono<Void> handleMessageCreate(MessageCreateEvent event) {
    long userId = event.getMessage().getAuthor().map(u -> u.getId().asLong()).orElse(-1L);
    if (userId == -1) return Mono.empty();

    if (!creationSessions.containsKey(userId)) {
      // L'utilisateur n'est pas en train de créer un personnage
      return Mono.empty();
    }

    Session<Characters> session = creationSessions.get(userId);
    String content = event.getMessage().getContent();

    switch (session.step) {
      case 0:
        session.entity.setCharacterName(content);
        session.step = 1;
        return ReplyFactory.reply(event, "What's your character's race?");

      case 1:
        session.entity.setRace(content);
        session.step = 2;
        return ReplyFactory.reply(event, "Which class does it have?");

      case 2:
        session.entity.setClass_(content);
        session.step = 3;
        return ReplyFactory.reply(event, "What is your character's background?");

      case 3:
        try {
          int level = Integer.parseInt(content);
          session.entity.setLevel(level);
        } catch (NumberFormatException e) {
          return ReplyFactory.reply(event, "What's his level?");
        }
        session.step = 4;
        return ReplyFactory.reply(event, "How many experience points does your character have?");

      case 4:
        try {
          int xp = Integer.parseInt(content);
          session.entity.setExperiencePoints(xp);
        } catch (NumberFormatException e) {
          return ReplyFactory.reply(event, "Please enter a valid integer for experience points.");
        }
        session.step = 5;
        return ReplyFactory.reply(event, "What are your character's features and traits?");

      case 5:
        session.entity.setFeaturesAndTraits(content);
        session.step = 6;
        return ReplyFactory.reply(event, "What languages does your character speak?");

      case 6:
        session.entity.setLanguages(content);
        session.step = 7;
        return ReplyFactory.reply(event, "Describe your character's personality traits.");

      case 7:
        session.entity.setPersonalityTraits(content);
        session.step = 8;

        session.entity.setDateCreated(LocalDateTime.now());
        session.entity.setLastUpdated(LocalDateTime.now());

        // Persist to database
        characterRepository.save(session.entity);

        // Clear session
        creationSessions.remove(userId);

        return ReplyFactory.reply(event, "Character created successfully! 🎉");

      default:
        return Mono.empty();
    }
  }

  public Mono<Void> handleModify(ButtonInteractionEvent event) {
    long userId = event.getInteraction().getUser().getId().asLong();
    String username = event.getInteraction().getUser().getUsername();

    logger.info("handleModify called for userId={}, username={}", userId, username);

    Session<Characters> session = new Session<>();
    session.step = 0;
    session.entity = new Characters();
    session.entity.setPlayerName(username);

    modificationSessions.put(userId, session);

    List<Characters> allPlayerCharacters = characterRepository.findByPlayerName(username);
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

  // Gère toute la logique conversationnelle
  public Mono<Void> handleMessageModify(MessageCreateEvent event) {
    long userId = event.getMessage().getAuthor().map(u -> u.getId().asLong()).orElse(-1L);
    if (userId == -1) return Mono.empty();

    if (!modificationSessions.containsKey(userId)) {
      return Mono.empty();
    }

    Session<Characters> session = creationSessions.get(userId);
    String content = event.getMessage().getContent();
    logger.info("handleMessageModify input: {}", content);

    logger.info(
        "userId={}, session exists={}, step={}",
        userId,
        session != null,
        session != null ? session.step : "N/A");

    switch (session.step) {
      case 0:
        Characters charToModify = characterRepository.findByCharacterName(content);
        logger.info(content);
        if (charToModify == null) {
          return ReplyFactory.reply(event, "I couldn't find this character. Try again?");
        }
        session.entity = charToModify;
        session.step = 1;
        return ReplyFactory.reply(event, "What do you want to update? (name, race, class, etc.)");

      case 1:
        if ("done".equalsIgnoreCase(content)) {
          modificationSessions.remove(userId);
          return ReplyFactory.reply(event, "All done! Character saved.");
        }
        session.lastField = content.toLowerCase();
        session.step = 2;
        return ReplyFactory.reply(
            event, "What is the new value for **" + session.lastField + "** ?");

      case 2:
        String field = session.lastField;
        switch (field) {
          case "name":
            session.entity.setCharacterName(content);
            break;
          case "race":
            session.entity.setRace(content);
            break;
          case "class":
            session.entity.setClass_(content);
            break;
          case "background":
            session.entity.setBackground(content);
            break;
          case "level":
            try {
              int level = Integer.parseInt(content);
              session.entity.setLevel(level);
            } catch (NumberFormatException e) {
              return ReplyFactory.reply(event, "Please enter a valid integer for the level.");
            }
            break;
          case "experiencepoints":
            try {
              int xp = Integer.parseInt(content);
              session.entity.setExperiencePoints(xp);
            } catch (NumberFormatException e) {
              return ReplyFactory.reply(
                  event, "Please enter a valid integer for experience points.");
            }
            break;
          case "featuresandtraits":
            session.entity.setFeaturesAndTraits(content);
            break;
          case "languages":
            session.entity.setLanguages(content);
            break;
          case "personalitytraits":
            session.entity.setPersonalityTraits(content);
            break;
          default:
            return ReplyFactory.reply(event, "I don't know this field. Try again.");
        }
    }

    session.entity.setLastUpdated(LocalDateTime.now());
    characterRepository.save(session.entity); // adapter ici si asynchrone
    session.step = 1; // Retour à l'étape choix du champ

    return ReplyFactory.reply(
        event, "Updated! Anything else? (name, race, class, etc.) Or type **done**.");
  }

  public Mono<Void> handleConsult(ButtonInteractionEvent event) {
    long userId = event.getInteraction().getUser().getId().asLong();
    String username = event.getInteraction().getUser().getUsername();

    logger.info("handleModify called for userId={}, username={}", userId, username);

    Session<Characters> session = new Session<>();
    session.step = 0;
    session.entity = new Characters();
    session.entity.setPlayerName(username);

    consultSessions.put(userId, session);

    List<Characters> allPlayerCharacters = characterRepository.findByPlayerName(username);
    StringBuilder charactersList = new StringBuilder();
    for (Characters c : allPlayerCharacters) {
      charactersList.append("- ").append(c.getCharacterName()).append("\n");
    }

    return ReplyFactory.deferAndSend(
        event,
        "Let's consult one of your characters!\nHere are your characters:\n"
            + charactersList
            + "\nWhich one do you want to see?");
  }

  public Mono<Void> handleMessageConsult(MessageCreateEvent event) {
    long userId = event.getMessage().getAuthor().map(u -> u.getId().asLong()).orElse(-1L);
    if (userId == -1) return Mono.empty();

    if (!consultSessions.containsKey(userId)) {
      return Mono.empty();
    }

    Session<Characters> session = creationSessions.get(userId);
    String content = event.getMessage().getContent();
    logger.info("handleMessageModify input: {}", content);
    logger.info(
        "userId={}, session exists={}, step={}",
        userId,
        session != null,
        session != null ? session.step : "N/A");

    Characters characterConsult = characterRepository.findByCharacterName(content);

    if (characterConsult == null) {
      // Gestion si pas trouvé
      return ReplyFactory.reply(event, "Character not found!");
    }

    StringBuilder characterDetails = new StringBuilder();
    characterDetails.append("**Character Details:**\n");
    characterDetails.append("Name: ").append(characterConsult.getCharacterName()).append("\n");
    characterDetails.append("Race: ").append(characterConsult.getRace()).append("\n");
    characterDetails.append("Class: ").append(characterConsult.getClass_()).append("\n");
    characterDetails.append("Background: ").append(characterConsult.getBackground()).append("\n");
    characterDetails.append("Level: ").append(characterConsult.getLevel()).append("\n");
    characterDetails
        .append("Experience Points: ")
        .append(characterConsult.getExperiencePoints())
        .append("\n");
    characterDetails
        .append("Features & Traits: ")
        .append(characterConsult.getFeaturesAndTraits())
        .append("\n");
    characterDetails.append("Languages: ").append(characterConsult.getLanguages()).append("\n");
    characterDetails
        .append("Personality Traits: ")
        .append(characterConsult.getPersonalityTraits())
        .append("\n");
    // Ajoute d'autres champs si besoin

    return ReplyFactory.reply(event, characterDetails.toString());
  }
}
