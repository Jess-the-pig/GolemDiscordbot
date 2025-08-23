package Golem.api.rpg.npcs.create_npcs;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.utils.FinalStepHandler;
import Golem.api.common.utils.GenericValidatedStepHandler;
import Golem.api.common.utils.Session;
import Golem.api.common.wrappers.ButtonInteractionEventWrapper;
import Golem.api.db.NpcsRepository;
import Golem.api.discordgetaway.DiscordEventHandler;
import Golem.api.rpg.dto.ReplyFactory;
import Golem.api.rpg.npcs.Npcs;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class NpcCreateService {
  private final Map<Long, Session<Npcs>> creationSessions = new HashMap<>();

  // Log et lien db
  private final NpcsRepository npcsRepository;

  // Pas des
  private final List<StepHandler<Npcs, ContentCarrier>> creationSteps;

  public NpcCreateService(NpcsRepository npcsRepository) {
    this.npcsRepository = npcsRepository;

    this.creationSteps =
        List.of(
            new GenericValidatedStepHandler<Npcs, ContentCarrier, String>(
                carrier -> carrier.getContent(), Npcs::setName, "Nom du NPC ?", ""),
            new GenericValidatedStepHandler<Npcs, ContentCarrier, Integer>(
                carrier -> Integer.parseInt(carrier.getContent()),
                Npcs::setBase_hp,
                "Le hp de base ?",
                "Please enter a valid number."),
            new GenericValidatedStepHandler<Npcs, ContentCarrier, Integer>(
                carrier -> Integer.parseInt(carrier.getContent()),
                Npcs::setStats_1,
                "Quel est la stat1 ?",
                "Please enter a valid number."),
            new GenericValidatedStepHandler<Npcs, ContentCarrier, Integer>(
                carrier -> Integer.parseInt(carrier.getContent()),
                Npcs::setStats_2,
                "Quel est la stat2 ?",
                "Please enter a valid number."),
            new GenericValidatedStepHandler<Npcs, ContentCarrier, Integer>(
                carrier -> Integer.parseInt(carrier.getContent()),
                Npcs::setStats_3,
                "Quel est la stat3 ?",
                "Please enter a valid number."),
            new GenericValidatedStepHandler<Npcs, ContentCarrier, Integer>(
                carrier -> Integer.parseInt(carrier.getContent()),
                Npcs::setStats_4,
                "Quel est la stat4 ?",
                "Please enter a valid number."),
            new GenericValidatedStepHandler<Npcs, ContentCarrier, Integer>(
                carrier -> Integer.parseInt(carrier.getContent()),
                Npcs::setStats_5,
                "Quel est la stat5 ?",
                "Please enter a valid number."),
            new GenericValidatedStepHandler<Npcs, ContentCarrier, Integer>(
                carrier -> Integer.parseInt(carrier.getContent()),
                Npcs::setStats_6,
                "Quel est la stat6 ?",
                "Please enter a valid number."),
            new GenericValidatedStepHandler<Npcs, ContentCarrier, String>(
                carrier -> carrier.getContent(),
                Npcs::setBackground,
                "Quel est le Background ?",
                ""),
            new GenericValidatedStepHandler<Npcs, ContentCarrier, String>(
                carrier -> carrier.getContent(), Npcs::setRace, "What Race ?", ""),
            new GenericValidatedStepHandler<Npcs, ContentCarrier, String>(
                carrier -> carrier.getContent(), Npcs::setClass_starting, "Starting class : ", ""),
            new GenericValidatedStepHandler<Npcs, ContentCarrier, Integer>(
                carrier -> Integer.parseInt(carrier.getContent()),
                Npcs::setClass_starting_level,
                "What class starting level ?",
                "Please enter a valid number."),
            new GenericValidatedStepHandler<Npcs, ContentCarrier, String>(
                carrier -> carrier.getContent(), Npcs::setSubclass_starting, "What subclass ?", ""),
            new GenericValidatedStepHandler<Npcs, ContentCarrier, String>(
                carrier -> carrier.getContent(), Npcs::setClass_other, "Other class?", ""),
            new GenericValidatedStepHandler<Npcs, ContentCarrier, String>(
                carrier -> carrier.getContent(), Npcs::setSubclass_other, "Other subclass?", ""),
            new GenericValidatedStepHandler<Npcs, ContentCarrier, Integer>(
                carrier -> Integer.parseInt(carrier.getContent()),
                Npcs::setTotal_level,
                "Total level ?",
                "Please enter a valid number."),
            new GenericValidatedStepHandler<Npcs, ContentCarrier, String>(
                carrier -> carrier.getContent(), Npcs::setFeats, "Feats ?", ""),
            new GenericValidatedStepHandler<Npcs, ContentCarrier, String>(
                carrier -> carrier.getContent(), Npcs::setInventory, "What Inventory ?", ""),
            new FinalStepHandler<>(
                (m, s) -> {}, // rien à setter
                npcsRepository::save,
                "Npc created successfully! 🎉"));
  }

  public List<DiscordEventHandler<?>> getEventHandlers() {
    return List.of(
        new DiscordEventHandler<>(ButtonInteractionEvent.class, this::handleMessageCreate));
  }

  public Mono<Void> handleMessageCreate(ButtonInteractionEvent event) {
    long userId = event.getInteraction().getUser().getId().asLong();
    if (userId == -1) return Mono.empty();

    Session<Npcs> session = creationSessions.get(userId);
    if (session == null) return Mono.empty();

    if (session.step >= creationSteps.size()) {
      creationSessions.remove(userId);
      return ReplyFactory.reply(event, "All done!");
    }

    StepHandler<Npcs, ContentCarrier> handler = creationSteps.get(session.step);

    ContentCarrier wrappedEvent = new ButtonInteractionEventWrapper(event);

    Mono<Void> result = handler.handle(wrappedEvent, session);

    if (handler instanceof FinalStepHandler) {
      creationSessions.remove(userId);
    }

    return result;
  }
}
