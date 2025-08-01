package Golem.api.rpg.monsters.create_monster;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.utils.FinalStepHandler;
import Golem.api.common.utils.GenericValidatedStepHandler;
import Golem.api.common.utils.Session;
import Golem.api.common.wrappers.ButtonInteractionEventWrapper;
import Golem.api.db.MonsterRepository;
import Golem.api.discordgetaway.DiscordEventHandler;
import Golem.api.rpg.dto.ReplyFactory;
import Golem.api.rpg.monsters.Monsters;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class MonsterCreateService {
  private final Map<Long, Session<Monsters>> creationSessions = new HashMap<>();

  // Log et lien db
  private final MonsterRepository monsterRepository;

  // Pas des
  private final List<StepHandler<Monsters, ContentCarrier>> creationSteps;

  public MonsterCreateService(MonsterRepository monsterRepository) {
    this.monsterRepository = monsterRepository;

    this.creationSteps =
        List.of(
            new GenericValidatedStepHandler<Monsters, ContentCarrier, String>(
                Function.identity(), Monsters::setName, "What's his image url?", ""),
            new GenericValidatedStepHandler<Monsters, ContentCarrier, String>(
                Function.identity(), Monsters::setUrl, "What's his cr ?", ""),
            new GenericValidatedStepHandler<Monsters, ContentCarrier, String>(
                Function.identity(), Monsters::setCr, "What's his type?", ""),
            new GenericValidatedStepHandler<Monsters, ContentCarrier, String>(
                Function.identity(), Monsters::setType, "What's his size?", ""),
            new GenericValidatedStepHandler<Monsters, ContentCarrier, String>(
                Function.identity(), Monsters::setSize, "What's his ac?", ""),
            new GenericValidatedStepHandler<Monsters, ContentCarrier, Integer>(
                Integer::parseInt,
                Monsters::setAc,
                "What's his hp?",
                "Please enter a valid number."),
            new GenericValidatedStepHandler<Monsters, ContentCarrier, Integer>(
                Integer::parseInt,
                Monsters::setHp,
                "What's his speed?",
                "Please enter a valid number."),
            new GenericValidatedStepHandler<Monsters, ContentCarrier, String>(
                Function.identity(), Monsters::setSpeed, "What's his alignement?", ""),
            new GenericValidatedStepHandler<Monsters, ContentCarrier, String>(
                Function.identity(), Monsters::setAlign, "Is he legendary ?", ""),
            new GenericValidatedStepHandler<Monsters, ContentCarrier, Boolean>(
                Boolean::parseBoolean,
                Monsters::setLegendary,
                "What's his source?",
                "Please enter a valid value"),
            new GenericValidatedStepHandler<Monsters, ContentCarrier, String>(
                Function.identity(), Monsters::setSource, "What is his strength?", ""),
            new GenericValidatedStepHandler<>(
                Integer::parseInt,
                Monsters::setStrScore,
                "What's his dexterity?",
                "Please enter a valid number."),
            new GenericValidatedStepHandler<>(
                Integer::parseInt,
                Monsters::setDexScore,
                "What's his constitution ?",
                "Please enter a valid number."),
            new GenericValidatedStepHandler<>(
                Integer::parseInt,
                Monsters::setConScore,
                "What's his intelligence ?",
                "Please enter a valid number."),
            new GenericValidatedStepHandler<>(
                Integer::parseInt,
                Monsters::setIntScore,
                "What's his wisdom ?",
                "Please enter a valid number."),
            new GenericValidatedStepHandler<>(
                Integer::parseInt,
                Monsters::setWisScore,
                "What's his charisma ?",
                "Please enter a valid number."),
            new GenericValidatedStepHandler<>(
                Integer::parseInt, Monsters::setChaScore, "", "Please enter a valid number."),
            new FinalStepHandler<>(
                (m, s) -> {}, // rien à setter
                monsterRepository::save,
                "Monster created successfully! 🎉"));
  }

  public List<DiscordEventHandler<?>> getEventHandlers() {
    return List.of(
        new DiscordEventHandler<>(ButtonInteractionEvent.class, this::handleMessageCreate));
  }

  public Mono<Void> handleMessageCreate(ButtonInteractionEvent event) {
    long userId = event.getInteraction().getUser().getId().asLong();
    if (userId == -1) return Mono.empty();

    Session<Monsters> session = creationSessions.get(userId);
    if (session == null) return Mono.empty();

    if (session.step >= creationSteps.size()) {
      creationSessions.remove(userId);
      return ReplyFactory.reply(event, "All done!");
    }

    StepHandler<Monsters, ContentCarrier> handler = creationSteps.get(session.step);

    ContentCarrier wrappedEvent = new ButtonInteractionEventWrapper(event);

    Mono<Void> result = handler.handle(wrappedEvent, session);

    if (handler instanceof FinalStepHandler) {
      creationSessions.remove(userId);
    }

    return result;
  }
}
