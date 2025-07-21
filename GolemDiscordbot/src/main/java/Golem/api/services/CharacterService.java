package Golem.api.services;

import Golem.api.entities.Characters;
import Golem.api.repositories.CharacterRepository;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CharacterService {
  private final Map<Long, CreationSession> creationSessions = new HashMap<>();
  private final CharacterRepository characterRepository;
  private static final Logger logger = LoggerFactory.getLogger(CharacterService.class);

  private static class CreationSession {
    int step = 0;
    Characters character = new Characters();
  }

  public Mono<Void> handleCreate(ButtonInteractionEvent event) {
    long userId = event.getInteraction().getUser().getId().asLong();
    String username = event.getInteraction().getUser().getUsername();

    logger.info("handleCreate called for userId={}, username={}", userId, username);

    CreationSession session = new CreationSession();
    session.step = 0;
    session.character.setPlayerName(username);

    creationSessions.put(userId, session);

    return event
        .deferReply()
        .withEphemeral(true)
        .then(
            event
                .getInteraction()
                .getChannel()
                .flatMap(
                    channel ->
                        channel.createMessage(
                            "Création de personnage : Quel est le nom du personnage ?"))
                .then());
  }

  public Mono<Void> handleMessageCreate(MessageCreateEvent event) {
    long userId = event.getMessage().getAuthor().map(u -> u.getId().asLong()).orElse(-1L);
    if (userId == -1) return Mono.empty();

    if (!creationSessions.containsKey(userId)) {
      // L'utilisateur n'est pas en train de créer un personnage
      return Mono.empty();
    }

    CreationSession session = creationSessions.get(userId);
    String content = event.getMessage().getContent();

    switch (session.step) {
      case 0:
        session.character.setCharacterName(content);
        session.step = 1;
        return event
            .getMessage()
            .getChannel()
            .flatMap(channel -> channel.createMessage("Quelle est la race du personnage ?"))
            .then();

      case 1:
        session.character.setRace(content);
        session.step = 2;
        return event
            .getMessage()
            .getChannel()
            .flatMap(channel -> channel.createMessage("Quelle est la classe du personnage ?"))
            .then();

      case 2:
        session.character.setClass_(content);
        session.step = 3;
        return event
            .getMessage()
            .getChannel()
            .flatMap(
                channel ->
                    channel.createMessage("Quel est le niveau du personnage ? (nombre entier)"))
            .then();

      case 3:
        try {
          int level = Integer.parseInt(content);
          session.character.setLevel(level);
        } catch (NumberFormatException e) {
          // Redemander si ce n’est pas un nombre
          return event
              .getMessage()
              .getChannel()
              .flatMap(
                  channel ->
                      channel.createMessage(
                          "Veuillez entrer un nombre entier valide pour le niveau."))
              .then();
        }
        session.step = 4;
        return event
            .getMessage()
            .getChannel()
            .flatMap(
                channel ->
                    channel.createMessage("Combien d'expérience points a-t-il ? (nombre entier)"))
            .then();

      case 4:
        try {
          int xp = Integer.parseInt(content);
          session.character.setExperiencePoints(xp);
        } catch (NumberFormatException e) {
          return event
              .getMessage()
              .getChannel()
              .flatMap(
                  channel ->
                      channel.createMessage(
                          "Veuillez entrer un nombre entier valide pour les points d'expérience."))
              .then();
        }

        // Remplissage des dates
        session.character.setDateCreated(LocalDateTime.now());
        session.character.setLastUpdated(LocalDateTime.now());

        // Sauvegarder en base
        characterRepository.save(session.character);

        // Fin de la session
        creationSessions.remove(userId);

        return event
            .getMessage()
            .getChannel()
            .flatMap(channel -> channel.createMessage("Personnage créé avec succès !"))
            .then();

      default:
        return Mono.empty();
    }
  }

  // Modifier
  public Mono<Void> handleModify(ButtonInteractionEvent event) {
    return event.reply("Ok, modification d'un personnage !").withEphemeral(true).then();
  }

  // consulter
  public Mono<Void> handleConsult(ButtonInteractionEvent event) {
    return event.reply("Ok, consultation des personnages !").withEphemeral(true).then();
  }
}
