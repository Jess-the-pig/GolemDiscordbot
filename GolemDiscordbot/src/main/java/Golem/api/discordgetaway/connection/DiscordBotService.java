package Golem.api.discordgetaway.connection;

import Golem.api.discordgetaway.slashcommands.CommandDispatcher;
import Golem.api.discordgetaway.slashcommands.RegisterSlashCommands;
import Golem.api.music.create_playlist.PlaylistCreateService;
import Golem.api.music.delete_playlist.PlaylistDeleteService;
import Golem.api.music.play_playlist.PlayPlaylistService;
import Golem.api.rpg.campaign.CampaignService;
import Golem.api.rpg.campaign.add_npc.AddNpcToCampaignService;
import Golem.api.rpg.characters.consult_characters.CharacterConsultService;
import Golem.api.rpg.characters.create_character.CharacterCreateService;
import Golem.api.rpg.characters.delete_character.CharacterDeleteService;
import Golem.api.rpg.characters.modify_character.CharacterModifyService;
import Golem.api.rpg.monsters.consult_monsters.MonsterConsultService;
import Golem.api.rpg.monsters.create_monster.MonsterCreateService;
import Golem.api.rpg.monsters.delete_monster.MonsterDeleteService;
import Golem.api.rpg.monsters.modify_monster.MonsterModifyService;
import Golem.api.rpg.npcs.consult_npcs.NpcConsultService;
import Golem.api.rpg.npcs.create_npcs.NpcCreateService;
import Golem.api.rpg.npcs.delete_npcs.DeleteNpcsService;
import Golem.api.rpg.npcs.modify_npcs.NpcsModifyService;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service Spring pour gérer le bot Discord.
 *
 * <p>Démarre le bot, enregistre les commandes slash, gère les interactions et publie les événements
 * dans Redis. Fournit également un mécanisme de shutdown propre.
 */
@Service("discordBotServiceV2")
@RequiredArgsConstructor
@Slf4j
public class DiscordBotService {

    private final CharacterCreateService characterCreateService;

    private final AddNpcToCampaignService addNpcToCampaignService;

    private final NpcsModifyService npcsModifyService;

    private final DeleteNpcsService deleteNpcsService;

    private final NpcCreateService npcCreateService;

    private final NpcConsultService npcConsultService;

    private final MonsterModifyService monsterModifyService;

    private final MonsterDeleteService monsterDeleteService;

    private final MonsterCreateService monsterCreateService;

    private final MonsterConsultService monsterConsultService;

    private final CharacterDeleteService characterDeleteService;

    private final CharacterConsultService characterConsultService;

    private final PlayPlaylistService playPlaylistService;

    private final PlaylistDeleteService playlistDeleteService;

    private final PlaylistCreateService playlistCreateService;

    private final CharacterModifyService characterModifyService;

    @Value("${DISCORD_TOKEN}")
    private String token;

    private GatewayDiscordClient client;

    private final CommandDispatcher dispatcher;
    private final RegisterSlashCommands registerSlashCommands;
    private final StringRedisTemplate redisTemplate;
    private final CampaignService campaignService;

    @PostConstruct
    public void startBot() {
        DiscordClient discordClient = DiscordClient.create(token);
        client = discordClient.login().block();

        if (client != null) {
            log.info("✅ Bot connected as: {}", client.getSelf().block().getUsername());

            // Enregistre toutes les commandes slash
            registerSlashCommands.registerSlashCommands(client, dispatcher.getCommands());
            // Encounters
            client.on(MessageCreateEvent.class, playlistCreateService::handleMessageCreate)
                    .subscribe();
            // Playlists
            client.on(MessageCreateEvent.class, playlistCreateService::handleMessageCreate)
                    .subscribe();
            client.on(MessageCreateEvent.class, playlistDeleteService::handleMessageDelete)
                    .subscribe();
            client.on(MessageCreateEvent.class, playPlaylistService::handleMessageSelect)
                    .subscribe();
            // Characters
            client.on(MessageCreateEvent.class, characterModifyService::handleMessageModify)
                    .subscribe();
            client.on(MessageCreateEvent.class, characterConsultService::handleMessageConsult)
                    .subscribe();
            client.on(MessageCreateEvent.class, characterDeleteService::handleMessageDelete)
                    .subscribe();
            client.on(MessageCreateEvent.class, characterCreateService::handleMessageCreate)
                    .subscribe();
            // Monsters
            client.on(MessageCreateEvent.class, monsterConsultService::handleMessageConsult)
                    .subscribe();
            client.on(MessageCreateEvent.class, monsterDeleteService::handleMessageDelete)
                    .subscribe();
            client.on(MessageCreateEvent.class, monsterModifyService::handleMessageModify)
                    .subscribe();
            // Npc
            client.on(MessageCreateEvent.class, npcConsultService::handleMessageConsult)
                    .subscribe();

            client.on(MessageCreateEvent.class, npcsModifyService::handleMessageModify).subscribe();
            // Campaign
            client.on(MessageCreateEvent.class, campaignService::handleCampaignMessage).subscribe();
            client.on(MessageCreateEvent.class, addNpcToCampaignService::handleMessageCreate)
                    .subscribe();
            client.on(MessageCreateEvent.class, playPlaylistService::handleMessageSelect)
                    .subscribe();

            client.on(
                            ChatInputInteractionEvent.class,
                            event -> {
                                String author = event.getInteraction().getUser().getUsername();
                                String command = event.getCommandName();
                                publishToRedisStream(
                                        "discord-events",
                                        Map.of("author", author, "command", command));
                                return dispatcher.handle(event);
                            })
                    .subscribe();

            // Handlers du dispatcher
            client.on(ButtonInteractionEvent.class, dispatcher::handleButton).subscribe();

            client.onDisconnect().block();
        }
    }

    /** Publie un message dans un stream Redis. */
    private void publishToRedisStream(String streamKey, Map<String, String> message) {
        StreamOperations<String, String, String> streamOps = redisTemplate.opsForStream();
        streamOps.add(streamKey, message);
    }

    /** Arrêt propre du bot. */
    @PreDestroy
    public void shutdown() {
        if (client != null) {
            client.logout().block();
        }
    }
}
