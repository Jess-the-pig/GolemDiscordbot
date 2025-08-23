package Golem.api.rpg.characters.create_character;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.utils.FinalStepHandler;
import Golem.api.common.utils.GenericValidatedStepHandler;
import Golem.api.common.utils.Session;
import Golem.api.common.wrappers.ButtonInteractionEventWrapper;
import Golem.api.common.wrappers.MessageCreateEventWrapper;
import Golem.api.db.CharacterRepository;
import Golem.api.rpg.characters.Characters;
import Golem.api.rpg.dto.ReplyFactory;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service pour créer un personnage de manière interactive via Discord.
 *
 * <p>Fonctionnalités principales : - Démarrage d'une session de création via bouton Discord -
 * Gestion de plusieurs étapes pour renseigner les attributs du personnage - Validation des valeurs
 * et sauvegarde finale du personnage dans la base de données
 */
@Service
@Slf4j
public class CharacterCreateService {

  private final Map<Long, Session<Characters>> creationSessions = new HashMap<>();

  private final CharacterRepository characterRepository;

  private final List<StepHandler<Characters, ContentCarrier>> creationSteps;

  public CharacterCreateService(CharacterRepository characterRepository) {
    this.characterRepository = characterRepository;

    this.creationSteps =
        List.of(
            new GenericValidatedStepHandler<Characters, ContentCarrier, String>(
                carrier -> carrier.getContent(), // 👈 transforme le ContentCarrier en String
                Characters::setCharacterName,
                "What's your race?",
                ""),
            new GenericValidatedStepHandler<Characters, ContentCarrier, String>(
                carrier -> carrier.getContent(),
                Characters::setRace,
                "Which class does it have?",
                ""),
            new GenericValidatedStepHandler<Characters, ContentCarrier, String>(
                carrier -> carrier.getContent(),
                Characters::setClass_,
                "What is your character's level?",
                ""),
            new GenericValidatedStepHandler<>(
                carrier -> Integer.parseInt(carrier.getContent()),
                Characters::setLevel,
                "How many experience points?",
                "Please enter a valid number for the level."),
            new GenericValidatedStepHandler<>(
                carrier -> Integer.parseInt(carrier.getContent()),
                Characters::setExperiencePoints,
                "What are your character's features and traits",
                "Please enter a valid integer for XP."),
            new GenericValidatedStepHandler<Characters, ContentCarrier, String>(
                carrier -> carrier.getContent(),
                Characters::setFeaturesAndTraits,
                "What languages does your character speak?",
                ""),
            new GenericValidatedStepHandler<Characters, ContentCarrier, String>(
                carrier -> carrier.getContent(),
                Characters::setLanguages,
                "Describe your character's personality traits.",
                ""),
            new FinalStepHandler<>(
                Characters::setPersonalityTraits,
                characterRepository::save,
                "Character created successfully! 🎉"));
  }

  /**
   * Gère les messages texte reçus pendant la session de création.
   *
   * @param event événement MessageCreateEvent
   * @return Mono<Void> représentant le traitement asynchrone
   */
  public Mono<Void> handleMessageCreate(MessageCreateEvent event) {
    long userId = event.getMessage().getUserData().id().asLong();

    Session<Characters> session = creationSessions.get(userId);
    if (session == null) {
      return Mono.empty();
    }

    if (session.step >= creationSteps.size()) {
      creationSessions.remove(userId);
      return ReplyFactory.reply(event, "All done!");
    }

    StepHandler<Characters, ContentCarrier> handler = creationSteps.get(session.step);

    ContentCarrier wrappedEvent =
        new MessageCreateEventWrapper(event); // ⚠️ Bien un WRAPPER pour ton event TEXTE

    Mono<Void> result = handler.handle(wrappedEvent, session);

    if (handler instanceof FinalStepHandler) {
      creationSessions.remove(userId);
    }

    return result;
  }

  /**
   * Démarre une session de création de personnage à partir d'un bouton Discord.
   *
   * @param event événement ButtonInteractionEvent
   * @return Mono<Void> représentant le traitement asynchrone
   */
  public Mono<Void> startCreationSession(ButtonInteractionEvent event) {
    long userId = event.getInteraction().getUser().getId().asLong();

    Session<Characters> session = new Session<>();
    session.entity = new Characters();
    session.step = 0;

    creationSessions.put(userId, session);

    StepHandler<Characters, ContentCarrier> handler = creationSteps.get(0);
    ContentCarrier wrappedEvent = new ButtonInteractionEventWrapper(event);

    return handler.handle(wrappedEvent, session);
  }
}
