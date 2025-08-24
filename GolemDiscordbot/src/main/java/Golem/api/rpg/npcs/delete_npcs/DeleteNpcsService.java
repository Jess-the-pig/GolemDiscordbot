package Golem.api.rpg.npcs.delete_npcs;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.utils.Session;
import Golem.api.common.wrappers.MessageCreateEventWrapper;
import Golem.api.db.NpcsRepository;
import Golem.api.rpg.characters.delete_character.DeleteEntityStepHandler;
import Golem.api.rpg.dto.ReplyFactory;
import Golem.api.rpg.npcs.Npcs;
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
public class DeleteNpcsService {

  private final NpcsRepository npcsRepository;
  private final Map<Long, Session<Npcs>> deleteSessions = new HashMap<>();
  private final List<StepHandler<Npcs, ContentCarrier>> deletionSteps;

  public Mono<Void> handleMessageDelete(ButtonInteractionEvent event) {
    long userId = event.getInteraction().getUser().getId().asLong();
    String username = event.getInteraction().getUser().getUsername();

    Session<Npcs> session = new Session<>();
    session.step = 0;
    session.entity = null; // Pas encore choisi
    deleteSessions.put(userId, session);

    List<Npcs> allPlayerNpcs = npcsRepository.findByUsername(username);
    if (allPlayerNpcs.isEmpty()) {
      return ReplyFactory.deferAndSend(event, "You don't have any characters to delete.");
    }

    StringBuilder npcList = new StringBuilder();
    for (Npcs n : allPlayerNpcs) {
      npcList.append("- ").append(n.getName()).append("\n");
    }

    return ReplyFactory.deferAndSend(
        event,
        "Let's delete your npc!\nHere are your npcs:\n"
            + npcList
            + "\nWhich one do you want to delete?");
  }

  public DeleteNpcsService(NpcsRepository npcsRepository) {
    this.npcsRepository = npcsRepository;
    this.deletionSteps =
        List.of(
            new DeleteEntityStepHandler<>(
                name -> npcsRepository.findByName(name), npcsRepository::delete));
  }

  public Mono<Void> handleMessageModify(MessageCreateEvent event) {
    long userId = event.getMessage().getAuthor().map(u -> u.getId().asLong()).orElse(-1L);
    if (userId == -1) return Mono.empty();

    Session<Npcs> session = deleteSessions.get(userId);
    if (session == null) return Mono.empty();

    if (session.step >= deletionSteps.size()) {
      deleteSessions.remove(userId);
      return ReplyFactory.reply(event, "All done!");
    }

    StepHandler<Npcs, ContentCarrier> handler = deletionSteps.get(session.step);

    // On crée un ContentCarrier à partir de MessageCreateEvent,
    // ici je suppose que tu as une classe wrapper adaptée
    ContentCarrier carrier = new MessageCreateEventWrapper(event);

    return handler.handle(carrier, session);
  }
}
