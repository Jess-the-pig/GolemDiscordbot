package Golem.api.rpg.characters.consult_characters;

import Golem.api.common.utils.Session;
import Golem.api.db.CharacterRepository;
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

/**
 * Service permettant à un utilisateur de consulter ses personnages.
 *
 * <p>Fonctionnalités principales : - Démarrage d'une session de consultation via un bouton Discord
 * - Gestion de la sélection de personnage par nom - Affichage des détails complets du personnage
 * sélectionné
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CharacterConsultService {

  private final Map<Long, Session<Characters>> consultSessions = new HashMap<>();
  private final CharacterRepository characterRepository;

  /**
   * Démarre une session de consultation pour un utilisateur à partir d'un bouton Discord.
   *
   * <p>Affiche la liste des personnages de l'utilisateur et invite à choisir lequel consulter.
   *
   * @param event événement de type ButtonInteractionEvent
   * @return Mono<Void> représentant le traitement asynchrone
   */
  public Mono<Void> handleConsult(ButtonInteractionEvent event) {
    long userId = event.getInteraction().getUser().getId().asLong();

    log.info("handleConsult called for userId={}", userId);

    // On démarre la session, étape 0 : choisir personnage
    Session<Characters> session = new Session<>();
    session.step = 0;
    session.entity = null; // Pas encore choisi

    consultSessions.put(userId, session);

    List<Characters> allPlayerCharacters = characterRepository.findByuserId(userId);
    if (allPlayerCharacters.isEmpty()) {
      return ReplyFactory.deferAndSend(event, "You don't have any characters to consult.");
    }

    StringBuilder charactersList = new StringBuilder();
    allPlayerCharacters.forEach(
        c -> charactersList.append("- ").append(c.getCharacterName()).append("\n"));

    return ReplyFactory.deferAndSend(
        event,
        "Let's consult one of your characters!\nHere are your characters:\n"
            + charactersList
            + "\nWhich one do you want to see?");
  }

  /**
   * Gère les messages texte pendant une session de consultation.
   *
   * <p>Si l'utilisateur fournit un nom de personnage valide, affiche ses détails.
   *
   * @param event événement MessageCreateEvent
   * @return Mono<Void> représentant le traitement asynchrone
   */
  public Mono<Void> handleMessageConsult(MessageCreateEvent event) {
    long userId = event.getMessage().getAuthor().map(u -> u.getId().asLong()).orElse(-1L);
    if (userId == -1) return Mono.empty();

    Session<Characters> session = consultSessions.get(userId);
    if (session == null) return Mono.empty();

    String content = event.getMessage().getContent().trim();
    log.info("handleMessageConsult input: {}", content);

    if (session.step == 0) {
      // On attend que l'utilisateur choisisse un personnage parmi la liste
      Characters characterConsult = characterRepository.findByCharacterName(content);

      if (characterConsult == null) {
        return ReplyFactory.reply(
            event, "Character not found! Please enter a valid character name.");
      }

      session.entity = characterConsult;
      session.step = 1;

      // Affiche les détails
      String details = buildCharacterDetails(characterConsult);
      consultSessions.remove(userId); // Consultation terminée

      return ReplyFactory.reply(event, details);
    }

    // Si étape inconnue, on supprime la session pour éviter blocage
    consultSessions.remove(userId);
    return Mono.empty();
  }

  /**
   * Construit une chaîne de texte contenant les informations détaillées d'un personnage.
   *
   * @param c le personnage à afficher
   * @return texte contenant le résumé du personnage
   */
  private String buildCharacterDetails(Characters c) {
    return new StringBuilder()
        .append("**Character Details:**\n")
        .append("Name: ")
        .append(c.getCharacterName())
        .append("\n")
        .append("Race: ")
        .append(c.getRace())
        .append("\n")
        .append("Class: ")
        .append(c.getClass_())
        .append("\n")
        .append("Background: ")
        .append(c.getBackground())
        .append("\n")
        .append("Level: ")
        .append(c.getLevel())
        .append("\n")
        .append("Experience Points: ")
        .append(c.getExperiencePoints())
        .append("\n")
        .append("Features & Traits: ")
        .append(c.getFeaturesAndTraits())
        .append("\n")
        .append("Languages: ")
        .append(c.getLanguages())
        .append("\n")
        .append("Personality Traits: ")
        .append(c.getPersonalityTraits())
        .append("\n")
        .toString();
  }
}
