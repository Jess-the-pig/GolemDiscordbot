package Golem.api.rpg.campaign.add_npc;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.utils.AddNpcsStepHandler;
import Golem.api.common.utils.FinalStepHandler;
import Golem.api.common.utils.Session;
import Golem.api.common.wrappers.MessageCreateEventWrapper;
import Golem.api.db.CampaignNpcRepository;
import Golem.api.db.CampaignRepository;
import Golem.api.db.NpcsRepository;
import Golem.api.rpg.campaign.CampaignNpc;
import Golem.api.rpg.dto.ReplyFactory;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service permettant d'ajouter un NPC existant à une campagne RPG.
 *
 * <p>Le processus se fait via une série d'étapes interactives gérées par des StepHandler : 1.
 * Demande l'ID de la campagne et associe la campagne correspondante. 2. Demande le nom du NPC et
 * l'associe si trouvé dans la base. 3. Étape finale qui sauvegarde l'association campagne-NPC.
 *
 * <p>Les sessions sont gérées par utilisateur grâce à la classe Session, ce qui permet de suivre la
 * progression de chaque ajout en cours.
 */
@Service
public class AddNpcToCampaignService {

  private final CampaignRepository campaignRepository;
  private final CampaignNpcRepository campaignNpcRepository;
  private final NpcsRepository npcsRepository;

  private final Map<Long, Session<CampaignNpc>> creationSessions = new HashMap<>();
  private final List<StepHandler<CampaignNpc, ContentCarrier>> creationSteps;

  public AddNpcToCampaignService(
      CampaignRepository campaignRepository,
      CampaignNpcRepository campaignNpcRepository,
      NpcsRepository npcsRepository) {
    this.campaignRepository = campaignRepository;
    this.campaignNpcRepository = campaignNpcRepository;
    this.npcsRepository = npcsRepository;

    this.creationSteps =
        List.of(
            new AddNpcsStepHandler(npcsRepository),
            new FinalStepHandler<>(
                (npc, ignored) -> {},
                campaignNpcRepository::save,
                "✅ Tous les NPCs ont été ajoutés à la campagne avec succès ! 🎉"));
  }

  /** Démarre une nouvelle session d'ajout de NPC à une campagne */
  public Mono<Void> handleAdd(ButtonInteractionEvent event) {
    long channelId = event.getInteraction().getChannelId().asLong();

    return Mono.fromCallable(() -> campaignRepository.findByCampaignId(channelId))
        .flatMap(
            optionalCampaign -> {
              if (optionalCampaign.isEmpty()) {
                // Répond une seule fois, pas de double reply possible
                return ReplyFactory.deferAndSend(
                    event,
                    "⚠️ Aucune campagne n'est liée à ce salon. Impossible d'ajouter un NPC.");
              }
              // Première réponse unique et definitive (deferAndSend)
              Session<CampaignNpc> session = new Session<>();
              session.entity = new CampaignNpc();
              session.entity.setCampaign(optionalCampaign.get());
              session.step = 0;
              creationSessions.put(channelId, session);
              return ReplyFactory.deferAndSend(
                  event, "✅ Session d'ajout de NPC démarrée ! Quel NPC veux-tu ajouter ?");
            });
  }

  /** Gère les messages envoyés pendant une session */
  public Mono<Void> handleMessageCreate(MessageCreateEvent event) {

    if (event.getMessage().getAuthor().map(user -> user.isBot()).orElse(false)) {
      return Mono.empty();
    }

    long channelId = event.getMessage().getChannelId().asLong();

    // On récupère la session existante
    Session<CampaignNpc> session = creationSessions.get(channelId);
    if (session == null) {
      return Mono.empty(); // aucune session en cours pour ce channel
    }

    StepHandler<CampaignNpc, ContentCarrier> handler = creationSteps.get(session.step);
    ContentCarrier wrappedEvent = new MessageCreateEventWrapper(event);

    Mono<Void> result = handler.handle(wrappedEvent, session);

    // Si on est arrivé à la fin, on nettoie la session
    if (handler instanceof FinalStepHandler) {
      creationSessions.remove(channelId);
    }

    return result;
  }
}
