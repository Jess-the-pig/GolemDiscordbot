package Golem.api.rpg.campaign.add_npc;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.utils.FinalStepHandler;
import Golem.api.common.utils.GenericValidatedStepHandler;
import Golem.api.common.utils.Session;
import Golem.api.common.wrappers.ButtonInteractionEventWrapper;
import Golem.api.common.wrappers.MessageCreateEventWrapper;
import Golem.api.db.CampaignNpcRepository;
import Golem.api.db.CampaignRepository;
import Golem.api.db.NpcsRepository;
import Golem.api.rpg.campaign.Campaign;
import Golem.api.rpg.campaign.CampaignNpc;
import Golem.api.rpg.dto.ReplyFactory;
import Golem.api.rpg.npcs.Npcs;
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
  private final List<StepHandler<CampaignNpc, ContentCarrier>> steps;

  /**
   * Constructeur. Initialise les étapes interactives d'ajout de NPC à une campagne.
   *
   * @param campaignRepository repository pour accéder aux campagnes
   * @param campaignNpcRepository repository pour gérer les associations campagne-NPC
   * @param npcsRepository repository pour accéder aux NPCs
   */
  public AddNpcToCampaignService(
      CampaignRepository campaignRepository,
      CampaignNpcRepository campaignNpcRepository,
      NpcsRepository npcsRepository) {
    this.campaignRepository = campaignRepository;
    this.campaignNpcRepository = campaignNpcRepository;
    this.npcsRepository = npcsRepository;

    this.steps =
        List.of(
            new GenericValidatedStepHandler<CampaignNpc, ContentCarrier, Long>(
                carrier -> Long.parseLong(carrier.getContent()), // 👈
                (npc, id) -> {
                  Campaign c =
                      campaignRepository
                          .findById(id)
                          .orElseThrow(() -> new IllegalArgumentException("❌ Campaign not found"));
                  npc.setCampaign(c);
                },
                "Quel est l'ID de la campagne ?",
                "Campagne introuvable. Essaie encore."),
            new GenericValidatedStepHandler<CampaignNpc, ContentCarrier, String>(
                carrier -> carrier.getContent(), // 👈 ici, explicite
                (npc, npcName) -> {
                  Npcs found = npcsRepository.findByName(npcName);
                  if (found == null) {
                    throw new IllegalArgumentException("❌ NPC introuvable");
                  }
                  npc.setNpc(found);
                },
                "Quel NPC veux-tu ajouter ? (nom exact du NPC)",
                "NPC introuvable. Essaie encore."),
            new FinalStepHandler<CampaignNpc, ContentCarrier>(
                (npc, ignored) -> {},
                campaignNpcRepository::save,
                "✅ NPC ajouté à la campagne avec succès ! 🎉"));
  }

  /**
   * Démarre une nouvelle session d'ajout de NPC à une campagne lorsqu'un utilisateur clique sur un
   * bouton.
   *
   * @param event événement d'interaction bouton Discord
   * @return un Mono vide une fois l'étape initiale traitée
   */
  public Mono<Void> handleAdd(ButtonInteractionEvent event) {
    long userId = event.getInteraction().getUser().getId().asLong();

    Session<CampaignNpc> session = new Session<>();
    session.entity = new CampaignNpc();
    session.step = 0;

    creationSessions.put(userId, session);

    StepHandler<CampaignNpc, ContentCarrier> handler = steps.get(0);
    ContentCarrier wrappedEvent = new ButtonInteractionEventWrapper(event);

    return handler.handle(wrappedEvent, session);
  }

  /**
   * Gère les messages texte envoyés par un utilisateur durant une session d'ajout de NPC à une
   * campagne.
   *
   * @param event événement de création de message Discord
   * @return un Mono vide après le traitement de l'étape courante
   */
  public Mono<Void> handleMessageCreate(MessageCreateEvent event) {
    long userId = event.getMessage().getUserData().id().asLong();

    Session<CampaignNpc> session = creationSessions.get(userId);
    if (session == null) {
      return Mono.empty();
    }

    if (session.step >= steps.size()) {
      creationSessions.remove(userId);
      return ReplyFactory.reply(event, "All done!");
    }

    StepHandler<CampaignNpc, ContentCarrier> handler = steps.get(session.step);
    ContentCarrier wrappedEvent = new MessageCreateEventWrapper(event);

    Mono<Void> result = handler.handle(wrappedEvent, session);

    if (handler instanceof FinalStepHandler) {
      creationSessions.remove(userId);
    }

    return result;
  }
}
