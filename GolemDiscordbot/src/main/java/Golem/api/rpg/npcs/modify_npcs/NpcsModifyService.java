package Golem.api.rpg.npcs.modify_npcs;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.utils.Session;
import Golem.api.common.wrappers.MessageCreateEventWrapper;
import Golem.api.db.CampaignNpcRepository;
import Golem.api.db.NpcsRepository;
import Golem.api.rpg.campaign.CampaignNpc;
import Golem.api.rpg.characters.modify_character.ChooseFieldStepHandler;
import Golem.api.rpg.characters.modify_character.SelectEntityStepHandler;
import Golem.api.rpg.characters.modify_character.UpdateFieldStepHandler;
import Golem.api.rpg.dto.ReplyFactory;
import Golem.api.rpg.npcs.Npcs;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class NpcsModifyService {
  private final NpcsRepository npcsRepository;
  private final CampaignNpcRepository campaignNpcRepository;

  private final Map<Long, Session<Npcs>> modificationSessions = new HashMap<>();

  // Setters adaptés à l'entité Npcs
  private final Map<String, BiConsumer<Npcs, Object>> npcsSetters = NpcsFieldSetters.SETTERS;

  private final List<StepHandler<Npcs, ContentCarrier>> modificationSteps;

  public NpcsModifyService(
      NpcsRepository npcsRepository, CampaignNpcRepository campaignNpcRepository) {
    this.npcsRepository = npcsRepository;
    this.campaignNpcRepository = campaignNpcRepository;

    this.modificationSteps =
        List.of(
            new SelectEntityStepHandler<>(npcsRepository::findByName),
            new ChooseFieldStepHandler<>(name -> npcsRepository.findByName(name), npcsSetters),
            new UpdateFieldStepHandler<>(npcsRepository::save, npcsSetters));
  }

  public Mono<Void> handleModify(ButtonInteractionEvent event, Long campaignId) {
    long userId = event.getInteraction().getUser().getId().asLong();

    Session<Npcs> session = new Session<>();
    session.step = 0;
    session.entity = null;
    modificationSessions.put(userId, session);

    List<Npcs> npcs;

    if (campaignId != null) {
      // Récupère uniquement les NPC liés à la campagne
      npcs =
          campaignNpcRepository.findByCampaignId(campaignId).stream()
              .map(CampaignNpc::getNpc)
              .toList();
    } else {
      // Sinon : tous les NPC universels accessibles au joueur
      npcs =
          npcsRepository.findAll().stream()
              .filter(m -> m.getUserid() == null || m.getUserid().equals(userId))
              .toList();
    }

    if (npcs.isEmpty()) {
      return event.reply("❌ Aucun PNJ trouvé.").withEphemeral(true).then();
    }

    StringBuilder npcsList = new StringBuilder();
    npcs.forEach(m -> npcsList.append("- ").append(m.getName()).append("\n"));

    return ReplyFactory.deferAndSend(
        event, "Voici les PNJ disponibles :\n" + npcsList + "\nLequel veux-tu modifier ?");
  }

  public Mono<Void> handleMessageModify(MessageCreateEvent event) {
    long userId = event.getMessage().getAuthor().map(u -> u.getId().asLong()).orElse(-1L);
    if (userId == -1) return Mono.empty();

    Session<Npcs> session = modificationSessions.get(userId);
    if (session == null) return Mono.empty();

    if (session.step >= modificationSteps.size()) {
      modificationSessions.remove(userId);
      return ReplyFactory.reply(event, "✅ Modification terminée !");
    }

    StepHandler<Npcs, ContentCarrier> handler = modificationSteps.get(session.step);
    ContentCarrier carrier = new MessageCreateEventWrapper(event);

    return handler.handle(carrier, session);
  }
}
