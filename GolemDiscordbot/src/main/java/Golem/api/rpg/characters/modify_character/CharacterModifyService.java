package Golem.api.rpg.characters.modify_character;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.utils.Session;
import Golem.api.common.wrappers.MessageCreateEventWrapper;
import Golem.api.db.CharacterRepository;
import Golem.api.rpg.characters.Characters;
import Golem.api.rpg.dto.ReplyFactory;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service pour la modification des personnages existants.
 *
 * <p>Ce service permet : - de démarrer une session de modification via un bouton Discord - de gérer
 * les messages texte pendant la session - de modifier les champs d'un personnage de façon
 * interactive
 */
@Service
@Slf4j
public class CharacterModifyService {

  private final CharacterRepository characterRepository;

  // La session
  private final Map<Long, Session<Characters>> modificationSessions = new HashMap<>();

  // Déclaration de la map avant l'utilisation
  Map<String, BiConsumer<Characters, Object>> characterSetters =
      Map.of(
          "Name", (c, v) -> c.setCharacterName((String) v),
          "Race", (c, v) -> c.setRace((String) v),
          "Class", (c, v) -> c.setClass_((String) v),
          "Background", (c, v) -> c.setBackground((String) v),
          "Level", (c, v) -> c.setLevel(Integer.parseInt(v.toString())),
          "Experience Points", (c, v) -> c.setExperiencePoints(Integer.parseInt(v.toString())),
          "Features and Traits", (c, v) -> c.setFeaturesAndTraits((String) v),
          "Languages", (c, v) -> c.setLanguages((String) v));
  ;

  private final List<StepHandler<Characters, ContentCarrier>> modificationSteps;

  public CharacterModifyService(CharacterRepository characterRepository) {
    this.characterRepository = characterRepository;
    this.modificationSteps =
        List.of(
            new SelectEntityStepHandler<Characters>(characterRepository::findByCharacterName),
            new ChooseFieldStepHandler<Characters>(
                name -> characterRepository.findByCharacterName(name), characterSetters),
            new UpdateFieldStepHandler<Characters>(characterRepository::save, characterSetters));
  }

  /**
   * Démarre une session de modification via un bouton Discord.
   *
   * @param event événement du bouton
   * @return Mono<Void> représentant le traitement asynchrone
   */
  public Mono<Void> handleModify(ButtonInteractionEvent event) {
    long userId = event.getInteraction().getUser().getId().asLong();

    Session<Characters> session = new Session<>();
    session.step = 0;
    session.entity = null; // Pas encore choisi
    modificationSessions.put(userId, session);

    List<Characters> allPlayerCharacters = characterRepository.findByuserId(userId);
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

  /**
   * Gère les messages texte pendant la session de modification.
   *
   * @param event événement du message
   * @return Mono<Void> représentant le traitement asynchrone
   */
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
}
