package Golem.api.rpg.campaign;

import Golem.api.common.utils.Session;
import Golem.api.db.CampaignRepository;
import Golem.api.db.CharacterRepository;
import Golem.api.rpg.characters.Characters;
import Golem.api.rpg.dto.ReplyFactory;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service gérant la création des campagnes RPG.
 *
 * <p>Cette classe permet de guider un utilisateur dans la création d'une campagne étape par étape
 * via des interactions Discord.
 *
 * <p>Étapes de création :
 *
 * <p>0 – Définir le nom de la campagne 1 – Définir le Dungeon Master (DM) 2 – Ajouter les joueurs
 * (séparés par virgules) jusqu’à validation avec "done" Une fois la campagne finalisée, elle est
 * sauvegardée dans la base de données.
 */
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

  /**
   * Gère les messages envoyés par l’utilisateur pendant la création d’une campagne. Chaque message
   * fait progresser le flux de création ou ajoute des joueurs à la campagne en cours.
   *
   * @param event événement de message créé
   * @return un flux réactif de traitement du message
   */
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

          return ReplyFactory.reply(
              event, "Players added! Add more or type **done** to finish the campaign creation.");
        }

      default:
        return Mono.empty();
    }
  }
}
