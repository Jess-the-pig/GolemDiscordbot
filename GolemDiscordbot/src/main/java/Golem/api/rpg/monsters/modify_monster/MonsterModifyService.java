package Golem.api.rpg.monsters.modify_monster;

import Golem.api.common.interfaces.ContentCarrier;
import Golem.api.common.interfaces.StepHandler;
import Golem.api.common.utils.Session;
import Golem.api.common.wrappers.MessageCreateEventWrapper;
import Golem.api.db.MonsterRepository;
import Golem.api.discordgetaway.DiscordEventHandler;
import Golem.api.rpg.characters.modify_character.ChooseFieldStepHandler;
import Golem.api.rpg.characters.modify_character.SelectEntityStepHandler;
import Golem.api.rpg.characters.modify_character.UpdateFieldStepHandler;
import Golem.api.rpg.dto.ReplyFactory;
import Golem.api.rpg.monsters.Monsters;
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
public class MonsterModifyService {
  private final MonsterRepository monsterRepository;

  private final Map<Long, Session<Monsters>> modificationSessions = new HashMap<>();

  Map<String, BiConsumer<Monsters, Object>> monsterSetters =
      Map.ofEntries(
          Map.entry("name", (BiConsumer<Monsters, Object>) (m, v) -> m.setName((String) v)),
          Map.entry("url", (BiConsumer<Monsters, Object>) (m, v) -> m.setUrl((String) v)),
          Map.entry("cr", (BiConsumer<Monsters, Object>) (m, v) -> m.setCr((String) v)),
          Map.entry("type", (BiConsumer<Monsters, Object>) (m, v) -> m.setType((String) v)),
          Map.entry("size", (BiConsumer<Monsters, Object>) (m, v) -> m.setSize((String) v)),
          Map.entry(
              "ac",
              (BiConsumer<Monsters, Object>) (m, v) -> m.setAc(Integer.parseInt(v.toString()))),
          Map.entry(
              "hp",
              (BiConsumer<Monsters, Object>) (m, v) -> m.setHp(Integer.parseInt(v.toString()))),
          Map.entry("speed", (BiConsumer<Monsters, Object>) (m, v) -> m.setSpeed((String) v)),
          Map.entry("align", (BiConsumer<Monsters, Object>) (m, v) -> m.setAlign((String) v)),
          Map.entry(
              "legendary",
              (BiConsumer<Monsters, Object>)
                  (m, v) -> m.setLegendary(Boolean.parseBoolean(v.toString()))),
          Map.entry("source", (BiConsumer<Monsters, Object>) (m, v) -> m.setSource((String) v)),
          Map.entry(
              "strength",
              (BiConsumer<Monsters, Object>)
                  (m, v) -> m.setStrScore(Integer.parseInt(v.toString()))),
          Map.entry(
              "dexterity",
              (BiConsumer<Monsters, Object>)
                  (m, v) -> m.setDexScore(Integer.parseInt(v.toString()))),
          Map.entry(
              "constitution",
              (BiConsumer<Monsters, Object>)
                  (m, v) -> m.setConScore(Integer.parseInt(v.toString()))),
          Map.entry(
              "intelligence",
              (BiConsumer<Monsters, Object>)
                  (m, v) -> m.setIntScore(Integer.parseInt(v.toString()))),
          Map.entry(
              "wisdom",
              (BiConsumer<Monsters, Object>)
                  (m, v) -> m.setWisScore(Integer.parseInt(v.toString()))),
          Map.entry(
              "charisma",
              (BiConsumer<Monsters, Object>)
                  (m, v) -> m.setChaScore(Integer.parseInt(v.toString()))));

  private final List<StepHandler<Monsters, ContentCarrier>> modificationSteps;

  public MonsterModifyService(MonsterRepository monsterRepository) {
    this.monsterRepository = monsterRepository;
    this.modificationSteps =
        List.of(
            new SelectEntityStepHandler<Monsters>(monsterRepository::findByName),
            new ChooseFieldStepHandler<Monsters>(
                name -> monsterRepository.findByName(name), monsterSetters),
            new UpdateFieldStepHandler<Monsters>(monsterRepository::save, monsterSetters));
  }

  public Mono<Void> handleModify(ButtonInteractionEvent event) {
    long userId = event.getInteraction().getUser().getId().asLong();
    String username = event.getInteraction().getUser().getUsername();

    Session<Monsters> session = new Session<>();
    session.step = 0;
    session.entity = null; // Pas encore choisi
    modificationSessions.put(userId, session);

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
        event,
        "Let's modify your monster !\nHere are your characters:\n"
            + monstersList
            + "\nWhich one do you want to modify?");
  }

  public List<DiscordEventHandler<?>> getEventHandlers() {
    return List.of(new DiscordEventHandler<>(MessageCreateEvent.class, this::handleMessageModify));
  }

  public Mono<Void> handleMessageModify(MessageCreateEvent event) {
    long userId = event.getMessage().getAuthor().map(u -> u.getId().asLong()).orElse(-1L);
    if (userId == -1) return Mono.empty();

    Session<Monsters> session = modificationSessions.get(userId);
    if (session == null) return Mono.empty();

    if (session.step >= modificationSteps.size()) {
      modificationSessions.remove(userId);
      return ReplyFactory.reply(event, "All done!");
    }

    StepHandler<Monsters, ContentCarrier> handler = modificationSteps.get(session.step);

    // On crée un ContentCarrier à partir de MessageCreateEvent,
    // ici je suppose que tu as une classe wrapper adaptée
    ContentCarrier carrier = new MessageCreateEventWrapper(event);

    return handler.handle(carrier, session);
  }
}
