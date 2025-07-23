package Golem.api.services;

import Golem.api.entities.Campaign;
import Golem.api.entities.Characters;
import Golem.api.factories.ReplyFactory;
import Golem.api.repositories.CampaignRepository;
import Golem.api.repositories.CharacterRepository;
import Golem.api.utils.Session;
import com.austinv11.servicer.Service;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CampaignService {

  private final Map<Long, Session<Campaign>> creationSessions = new HashMap<>();
  private final CampaignRepository campaignRepository;
  private final CharacterRepository characterRepository;

  public Mono<Void> handleCreate(ButtonInteractionEvent event) {
    Long campaignId = event.getInteraction().getChannelId().asLong();

    Session<Campaign> session = new Session<>();
    session.step = 0;
    session.entity = new Campaign();
    session.entity.setCampaignId(campaignId);

    creationSessions.put(campaignId, session);

    return ReplyFactory.deferAndSend(event, "Let's create our campaign ! What's the name ?");
  }

  public Mono<Void> handleMessageCreate(MessageCreateEvent event) {

    Long campaignId = event.getMessage().getChannelId().asLong();
    String userId = event.getMessage().getUserData().id().asString();

    if (!creationSessions.containsKey(campaignId)) {
      return Mono.empty();
    }

    Session<Campaign> session = creationSessions.get(campaignId);
    String content = event.getMessage().getContent();

    switch (session.step) {
      case 0:
        session.entity.setName(content);
        session.step = 1;
        return ReplyFactory.reply(event, "Who is the dm ? ");

      case 1:
        session.entity.setDm(content);
        session.step = 2;
        return ReplyFactory.reply(event, "Who are the players ? (separate names by commas)");

      case 2:
        String contentLower = content.trim().toLowerCase();

        if ("done".equals(contentLower)) {
          // Finaliser la campagne
          session.entity.setDateCreated(LocalDateTime.now());
          session.entity.setLastUpdated(LocalDateTime.now());
          session.entity.setPlayerCreator(userId);

          campaignRepository.save(session.entity);

          creationSessions.remove(campaignId);

          return ReplyFactory.reply(
              event,
              "Campaign created successfully with players! 🎉 You can now use /chest, /loot,"
                  + " /encounter commands in this chat.");
        } else {
          // Ajouter les joueurs listés (séparés par virgule)
          String[] playerNames = content.split(",");

          if (session.entity.getCharacters() == null) {
            session.entity.setCharacters(new ArrayList<>());
          }

          for (String playerNameRaw : playerNames) {
            String playerName = playerNameRaw.trim();
            Characters character = characterRepository.findByCharacterName(playerName);

            if (character != null) {
              session.entity.getCharacters().add(character);
            } else {
              return ReplyFactory.reply(
                  event, "Character named '" + playerName + "' not found. Please check the name.");
            }
          }

          // Rester à l'étape 2 pour ajouter d'autres joueurs
          return ReplyFactory.reply(
              event, "Players added! Add more or type **done** to finish the campaign creation.");
        }

      default:
        return Mono.empty();
    }
  }
}
