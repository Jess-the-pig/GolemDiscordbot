package Golem.api.rpg.campaign;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.utils.AddPlayersStepHandler;
import Golem.api.common.utils.FinalStepHandlerSimple;
import Golem.api.common.utils.GenericValidatedStepHandler;
import Golem.api.common.utils.Session;
import Golem.api.common.wrappers.MessageCreateEventWrapper;
import Golem.api.db.CampaignRepository;
import Golem.api.db.CharacterRepository;
import Golem.api.discordgetaway.DiscordEventHandler;
import Golem.api.discordgetaway.DiscordEventHandlerProvider;
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
public class CampaignService implements DiscordEventHandlerProvider {

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

  @Override
  public List<DiscordEventHandler<?>> getEventHandlers() {
    log.info("[CampaignService] Registering MessageCreateEvent handler");
    return List.of(
        new DiscordEventHandler<>(MessageCreateEvent.class, this::handleCampaignMessage));
  }

  /** Gère les messages pour la création de campagne, étape par étape. */
  public Mono<Void> handleCampaignMessage(MessageCreateEvent event) {
    // Ignore les messages du bot lui-même
    if (event.getMessage().getAuthor().map(user -> user.isBot()).orElse(false)) {
      return Mono.empty();
    }

    long channelId = event.getMessage().getChannelId().asLong();
    Session<Campaign> session = creationSessions.get(channelId);

    // Si aucune session n'existe, on ignore
    if (session == null) {
      log.info("[CampaignService] No active session for channel {}", channelId);
      return Mono.empty();
    }

    // Si toutes les étapes sont terminées, on supprime la session
    if (session.step >= creationSteps.size()) {
      creationSessions.remove(channelId);
      log.info("[CampaignService] Campaign creation finished for channel {}", channelId);
      return ReplyFactory.reply(event, "All done!");
    }

    StepHandler<Campaign, ContentCarrier> handler = creationSteps.get(session.step);
    ContentCarrier wrappedEvent = new MessageCreateEventWrapper(event);

    log.info("[CampaignService] Handling step {} for channel {}", session.step, channelId);
    log.info("Current campaign state: {}", session.entity.getName());
    log.info("Current campaign state: {}", session.entity.getDm());

    return handler
        .handle(wrappedEvent, session)
        .doOnSuccess(
            v -> {
              if (session.step >= creationSteps.size()) {
                creationSessions.remove(channelId);
                log.info("[CampaignService] Campaign session removed for channel {}", channelId);
              }
            });
  }
}
