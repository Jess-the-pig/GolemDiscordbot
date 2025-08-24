package Golem.api.rpg.monsters.delete_monster;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.utils.Session;
import Golem.api.common.wrappers.MessageCreateEventWrapper;
import Golem.api.db.MonsterRepository;
import Golem.api.rpg.characters.delete_character.DeleteEntityStepHandler;
import Golem.api.rpg.dto.ReplyFactory;
import Golem.api.rpg.monsters.Monsters;
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
public class MonsterDeleteService {
  private final MonsterRepository monsterRepository;
  private final Map<Long, Session<Monsters>> deleteSessions = new HashMap<>();
  private final List<StepHandler<Monsters, ContentCarrier>> deletionSteps;

  public Mono<Void> handleMessageDelete(ButtonInteractionEvent event) {
    long userId = event.getInteraction().getUser().getId().asLong();
    String username = event.getInteraction().getUser().getUsername();

    Session<Monsters> session = new Session<>();
    session.step = 0;
    session.entity = null; // Pas encore choisi
    deleteSessions.put(userId, session);

    List<Monsters> allPlayerMonsters = monsterRepository.findByUsername(username);
    if (allPlayerMonsters.isEmpty()) {
      return ReplyFactory.deferAndSend(event, "You don't have any characters to delete.");
    }

    StringBuilder monstersList = new StringBuilder();
    for (Monsters m : allPlayerMonsters) {
      monstersList.append("- ").append(m.getName()).append("\n");
    }

    return ReplyFactory.deferAndSend(
        event,
        "Let's delete your monster!\nHere are your monsters:\n"
            + monstersList
            + "\nWhich one do you want to delete?");
  }

  public MonsterDeleteService(MonsterRepository monsterRepository) {
    this.monsterRepository = monsterRepository;
    this.deletionSteps =
        List.of(
            new DeleteEntityStepHandler<>(
                name -> monsterRepository.findByName(name), monsterRepository::delete));
  }

  public Mono<Void> handleMessageModify(MessageCreateEvent event) {
    long userId = event.getMessage().getAuthor().map(u -> u.getId().asLong()).orElse(-1L);
    if (userId == -1) return Mono.empty();

    Session<Monsters> session = deleteSessions.get(userId);
    if (session == null) return Mono.empty();

    if (session.step >= deletionSteps.size()) {
      deleteSessions.remove(userId);
      return ReplyFactory.reply(event, "All done!");
    }

    StepHandler<Monsters, ContentCarrier> handler = deletionSteps.get(session.step);

    // On crée un ContentCarrier à partir de MessageCreateEvent,
    // ici je suppose que tu as une classe wrapper adaptée
    ContentCarrier carrier = new MessageCreateEventWrapper(event);

    return handler.handle(carrier, session);
  }
}
