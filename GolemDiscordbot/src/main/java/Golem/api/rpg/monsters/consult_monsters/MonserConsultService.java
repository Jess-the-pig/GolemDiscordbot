package Golem.api.rpg.monsters.consult_monsters;

import Golem.api.common.utils.Session;
import Golem.api.db.MonsterRepository;
import Golem.api.rpg.dto.ReplyFactory;
import Golem.api.rpg.monsters.Monsters;
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
public class MonserConsultService {
  private final Map<Long, Session<Monsters>> consultSessions = new HashMap<>();
  private final MonsterRepository monsterRepository;

  public Mono<Void> handleConsult(ButtonInteractionEvent event) {
    long userId = event.getInteraction().getUser().getId().asLong();
    String username = event.getInteraction().getUser().getUsername();

    log.info("handleConsult called for userId={}, username={}", userId, username);

    // Créer une session de consultation
    Session<Monsters> session = new Session<>();
    session.step = 0;
    session.entity = null;
    consultSessions.put(userId, session);

    List<Monsters> allMonsters = monsterRepository.findAll();

    List<Monsters> playerMonsters =
        allMonsters.stream()
            .filter(m -> m.getUserid() == null || m.getUserid().equals(userId))
            .toList();

    if (playerMonsters.isEmpty()) {
      return event.reply("❌ Aucun monstre trouvé pour toi.").withEphemeral(true).then();
    }

    StringBuilder monstersList = new StringBuilder();
    playerMonsters.forEach(m -> monstersList.append("- ").append(m.getName()).append("\n"));

    return ReplyFactory.deferAndSend(
        event, "Voici tes monstres et les monstres génériques :\n" + monstersList);
  }

  public Mono<Void> handleMessageConsult(MessageCreateEvent event) {
    long userId = event.getMessage().getAuthor().map(u -> u.getId().asLong()).orElse(-1L);
    if (userId == -1) return Mono.empty();

    Session<Monsters> session = consultSessions.get(userId);
    if (session == null) return Mono.empty();

    String content = event.getMessage().getContent().trim();
    log.info("handleMessageConsult input: {}", content);

    if (session.step == 0) {
      // On attend que l'utilisateur choisisse un personnage parmi la liste
      Monsters monsterConsult = monsterRepository.findByName(content);

      if (monsterConsult == null) {
        return ReplyFactory.reply(event, "Monster not found! Please enter a valid Monster name.");
      }

      session.entity = monsterConsult;
      session.step = 1;

      // Affiche les détails
      String details = buildMonsterDetails(monsterConsult);
      consultSessions.remove(userId); // Consultation terminée

      return ReplyFactory.reply(event, details);
    }

    // Si étape inconnue, on supprime la session pour éviter blocage
    consultSessions.remove(userId);
    return Mono.empty();
  }

  private String buildMonsterDetails(Monsters m) {
    return new StringBuilder()
        .append("**Monster Details:**\n")
        .append("Name: ")
        .append(m.getName())
        .append("\n")
        .append("Cr: ")
        .append(m.getCr())
        .append("\n")
        .append(" Type: ")
        .append(m.getType())
        .append("\n")
        .append("Size: ")
        .append(m.getSize())
        .append("\n")
        .append("Ac :")
        .append(m.getAc())
        .append("\n")
        .append("Hp: ")
        .append(m.getHp())
        .append("\n")
        .append("Speed: ")
        .append(m.getSpeed())
        .append("\n")
        .append("Alignement: ")
        .append(m.getAlign())
        .append("\n")
        .append("Source: ")
        .append(m.getSource())
        .append("\n")
        .append("Strength: ")
        .append(m.getStrScore())
        .append("\n")
        .append("Dexterity: ")
        .append(m.getDexScore())
        .append("\n")
        .append("Constitution: ")
        .append(m.getConScore())
        .append("\n")
        .append("Intelligence: ")
        .append(m.getIntScore())
        .append("\n")
        .append("Wisdom: ")
        .append(m.getWisScore())
        .append("\n")
        .append("Charisma: ")
        .append(m.getChaScore())
        .toString();
  }
}
