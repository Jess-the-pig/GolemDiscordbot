package Golem.api.rpg.characters.create_character;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.utils.FinalStepHandler;
import Golem.api.common.utils.GenericValidatedStepHandler;
import Golem.api.common.utils.Session;
import Golem.api.common.wrappers.ButtonInteractionEventWrapper;
import Golem.api.db.CharacterRepository;
import Golem.api.discordgetaway.DiscordEventHandler;
import Golem.api.rpg.characters.Characters;
import Golem.api.rpg.dto.ReplyFactory;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CharacterCreateService {
  // Creation de la session
  private final Map<Long, Session<Characters>> creationSessions = new HashMap<>();

  // Log et lien db
  private final CharacterRepository characterRepository;

  // Pas des
  private final List<StepHandler<Characters, ContentCarrier>> creationSteps;

  public CharacterCreateService(CharacterRepository characterRepository) {
    this.characterRepository = characterRepository;

    this.creationSteps =
        List.of(
            new GenericValidatedStepHandler<Characters, ContentCarrier, String>(
                Function.identity(), Characters::setCharacterName, "What's your race?", ""),
            new GenericValidatedStepHandler<Characters, ContentCarrier, String>(
                Function.identity(), Characters::setRace, "Which class does it have?", ""),
            new GenericValidatedStepHandler<Characters, ContentCarrier, String>(
                Function.identity(), Characters::setClass_, "What is your character's level?", ""),
            new GenericValidatedStepHandler<>(
                Integer::parseInt,
                Characters::setLevel,
                "How many experience points?",
                "Please enter a valid number for the level."),
            new GenericValidatedStepHandler<>(
                Integer::parseInt,
                Characters::setExperiencePoints,
                "What are your character's features and traits",
                "Please enter a valid integer for XP."),
            new GenericValidatedStepHandler<Characters, ContentCarrier, String>(
                Function.identity(),
                Characters::setFeaturesAndTraits,
                "What languages does your character speak?",
                ""),
            new GenericValidatedStepHandler<Characters, ContentCarrier, String>(
                Function.identity(),
                Characters::setLanguages,
                "Describe your character's personality traits.",
                ""),
            new FinalStepHandler<>(
                Characters::setPersonalityTraits,
                characterRepository::save,
                "Character created successfully! 🎉"));
  }

  public List<DiscordEventHandler<?>> getEventHandlers() {
    return List.of(
        new DiscordEventHandler<>(ButtonInteractionEvent.class, this::handleMessageCreate));
  }

  public Mono<Void> handleMessageCreate(ButtonInteractionEvent event) {
    long userId = event.getInteraction().getUser().getId().asLong();
    if (userId == -1) return Mono.empty();

    Session<Characters> session = creationSessions.get(userId);
    if (session == null) return Mono.empty();

    if (session.step >= creationSteps.size()) {
      creationSessions.remove(userId);
      return ReplyFactory.reply(event, "All done!");
    }

    StepHandler<Characters, ContentCarrier> handler = creationSteps.get(session.step);

    ContentCarrier wrappedEvent = new ButtonInteractionEventWrapper(event);

    Mono<Void> result = handler.handle(wrappedEvent, session);

    if (handler instanceof FinalStepHandler) {
      creationSessions.remove(userId);
    }

    return result;
  }
}
