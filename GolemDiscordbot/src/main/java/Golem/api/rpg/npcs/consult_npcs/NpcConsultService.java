package Golem.api.rpg.npcs.consult_npcs;

import Golem.api.common.utils.Session;
import Golem.api.db.NpcsRepository;
import Golem.api.discordgetaway.DiscordEventHandler;
import Golem.api.rpg.dto.ReplyFactory;
import Golem.api.rpg.npcs.Npcs;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class NpcConsultService {

  private final Map<Long, Session<Npcs>> consultSessions = new HashMap<>();
  private final NpcsRepository npcsRepository;

  public List<DiscordEventHandler<?>> getEventHandlers() {
    log.info("NpcsConsultService loaded");
    return List.of(new DiscordEventHandler<>(MessageCreateEvent.class, this::handleMessageConsult));
  }

  public Mono<Void> handleConsult(ButtonInteractionEvent event) {
    long userId = event.getInteraction().getUser().getId().asLong();
    String username = event.getInteraction().getUser().getUsername();

    log.info("handleConsult called for userId={}, username={}", userId, username);

    // Créer une session de consultation
    Session<Npcs> session = new Session<>();
    session.step = 0;
    session.entity = null;
    consultSessions.put(userId, session);

    List<Npcs> allNpcs = npcsRepository.findAll();

    List<Npcs> playerNpcs =
        allNpcs.stream()
            .filter(m -> m.getUserid() == null || m.getUserid().equals(userId))
            .toList();

    if (playerNpcs.isEmpty()) {
      return event.reply("❌ Aucun npc trouvé pour toi.").withEphemeral(true).then();
    }

    StringBuilder npcsList = new StringBuilder();
    playerNpcs.forEach(m -> npcsList.append("- ").append(m.getName()).append("\n"));

    return ReplyFactory.deferAndSend(
        event, "Voici tes monstres et les monstres génériques :\n" + npcsList);
  }

  public Mono<Void> handleMessageConsult(MessageCreateEvent event) {
    long userId = event.getMessage().getAuthor().map(u -> u.getId().asLong()).orElse(-1L);
    if (userId == -1) return Mono.empty();

    Session<Npcs> session = consultSessions.get(userId);
    if (session == null) return Mono.empty();

    String content = event.getMessage().getContent().trim();
    log.info("handleMessageConsult input: {}", content);

    if (session.step == 0) {
      // On attend que l'utilisateur choisisse un personnage parmi la liste
      Npcs npcsConsult = npcsRepository.findByName(content);

      if (npcsConsult == null) {
        return ReplyFactory.reply(event, "Npc introuvable.");
      }

      session.entity = npcsConsult;
      session.step = 1;

      // Affiche les détails
      String details = buildNpcDetails(npcsConsult);
      consultSessions.remove(userId); // Consultation terminée

      return ReplyFactory.reply(event, details);
    }

    // Si étape inconnue, on supprime la session pour éviter blocage
    consultSessions.remove(userId);
    return Mono.empty();
  }

  private String buildNpcDetails(Npcs n) {
    return new StringBuilder()
        .append("**Npcs Details:**\n")
        .append("Name: ")
        .append(n.getName())
        .append("\n")
        .append("Hp: ")
        .append(n.getBase_hp())
        .append("\n")
        .append(" Stat 1: ")
        .append(n.getStats_1())
        .append("\n")
        .append(" Stat 2: ")
        .append(n.getStats_2())
        .append("\n")
        .append(" Stat 3: ")
        .append(n.getStats_3())
        .append("\n")
        .append(" Stat 4: ")
        .append(n.getStats_4())
        .append("\n")
        .append(" Stat 5: ")
        .append(n.getStats_5())
        .append("\n")
        .append(" Stat 6: ")
        .append(n.getStats_6())
        .append("\n")
        .append(" Background: ")
        .append(n.getBackground())
        .append("\n")
        .append(" Race: ")
        .append(n.getRace())
        .append("\n")
        .append(" Starting class: ")
        .append(n.getClass_starting())
        .append("\n")
        .append(" CLass starting level: ")
        .append(n.getClass_starting_level())
        .append("\n")
        .append(" Subclass starting: ")
        .append(n.getSubclass_starting())
        .append("\n")
        .append(" Other classes: ")
        .append(n.getClass_other())
        .append("\n")
        .append(" Other subclasses: ")
        .append(n.getSubclass_other())
        .append("\n")
        .append(" Total level: ")
        .append(n.getTotal_level())
        .append("\n")
        .append(" Feats: ")
        .append(n.getFeats())
        .append("\n")
        .append(" Inventory: ")
        .append(n.getInventory())
        .append("\n")
        .append(" Notes: ")
        .append(n.getNotes_len())
        .append("\n")
        .toString();
  }
}
