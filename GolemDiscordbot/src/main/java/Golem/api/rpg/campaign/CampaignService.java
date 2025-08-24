package Golem.api.rpg.campaign;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.utils.AddPlayersStepHandler;
import Golem.api.common.utils.FinalStepHandler;
import Golem.api.common.utils.FinalStepHandlerSimple;
import Golem.api.common.utils.GenericValidatedStepHandler;
import Golem.api.common.utils.Session;
import Golem.api.common.wrappers.ButtonInteractionEventWrapper;
import Golem.api.common.wrappers.MessageCreateEventWrapper;
import Golem.api.db.CampaignRepository;
import Golem.api.db.CharacterRepository;
import Golem.api.rpg.dto.ReplyFactory;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CampaignService {

  private final CampaignRepository campaignRepository;
  private final CharacterRepository characterRepository;

  private final List<StepHandler<Campaign, ContentCarrier>> creationSteps;
  private final Map<Long, Session<Campaign>> creationSessions = new HashMap<>();

  public CampaignService(
      CampaignRepository campaignRepository, CharacterRepository characterRepository) {
    this.campaignRepository = campaignRepository;
    this.characterRepository = characterRepository;

    creationSteps =
        List.of(
            new GenericValidatedStepHandler<Campaign, ContentCarrier, String>(
                carrier -> carrier.getContent(),
                null,
                "What's the campaign name?", // 1ère question ici !
                "Invalid name, please try again."),
            new GenericValidatedStepHandler<Campaign, ContentCarrier, String>(
                carrier -> carrier.getContent(),
                Campaign::setName,
                "Who is the dungeon master?", // 2ème question ici
                "Invalid DM, please try again."),
            new GenericValidatedStepHandler<Campaign, ContentCarrier, String>(
                carrier -> carrier.getContent(),
                Campaign::setDm,
                "What characters are you going to add?", // 2ème question ici
                "Invalid DM, please try again."),
            new AddPlayersStepHandler(characterRepository),
            new FinalStepHandlerSimple<Campaign, ContentCarrier>(
                campaignRepository::save, "Campaign created successfully! 🎉"));
  }

  /** Démarre la création d'une campagne à partir d'un bouton. */
  public Mono<Void> startCampaignCreation(ButtonInteractionEvent event) {
    long channelId = event.getInteraction().getChannelId().asLong();
    Session<Campaign> session = new Session<>();
    session.entity = new Campaign();
    session.step = 0;
    creationSessions.put(channelId, session);

    log.info("[CampaignService] Session started for channel {}", channelId);

    // ⚠️ ne pas poser la 1ère question ici
    return ReplyFactory.deferAndSend(event, "Campaign creation started! Please answer below 👇");
  }

  public Mono<Void> handleCampaignMessage(MessageCreateEvent event) {
    long channelId = event.getMessage().getChannelId().asLong();

    Session<Campaign> session = creationSessions.get(channelId);
    if (session == null) {
      // aucune session active → on ignore
      return Mono.empty();
    }

    if (session.step >= creationSteps.size()) {
      // normalement déjà fini, on supprime pour être sûr
      creationSessions.remove(channelId);
      return ReplyFactory.reply(event, "All done!");
    }

    StepHandler<Campaign, ContentCarrier> handler = creationSteps.get(session.step);
    ContentCarrier wrappedEvent = new MessageCreateEventWrapper(event);

    return handler
        .handle(wrappedEvent, session)
        .then(
            Mono.defer(
                () -> {
                  // ✅ si c’est le dernier handler (= sauvegarde),
                  // on supprime définitivement la session
                  if (handler instanceof FinalStepHandler) {
                    creationSessions.remove(channelId);
                  }
                  return Mono.empty();
                }));
  }

  public Mono<Void> startCampaignSession(ButtonInteractionEvent event) {
    long channelId = event.getInteraction().getChannelId().asLong();

    Session<Campaign> session = new Session<>();
    session.entity = new Campaign();
    session.step = 0;

    creationSessions.put(channelId, session);

    StepHandler<Campaign, ContentCarrier> handler = creationSteps.get(0);
    ContentCarrier wrappedEvent = new ButtonInteractionEventWrapper(event);

    return handler.handle(wrappedEvent, session);
  }
}
