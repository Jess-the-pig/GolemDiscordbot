package Golem.api.commands.implementation;

import Golem.api.commands.ICommand;
import Golem.api.services.LavaPlayerAudioProvider;
import Golem.api.services.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import java.util.List;
import reactor.core.publisher.Mono;

public class playCommand implements ICommand {

  private final AudioPlayerManager playerManager;
  private final AudioPlayer player;
  private final TrackScheduler scheduler;
  private final LavaPlayerAudioProvider provider;

  public playCommand(
      AudioPlayerManager playerManager,
      AudioPlayer player,
      TrackScheduler scheduler,
      LavaPlayerAudioProvider provider) {
    this.playerManager = playerManager;
    this.player = player;
    this.scheduler = scheduler;
    this.provider = provider;
  }

  @Override
  public String getName() {
    return "play";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    String url =
        event
            .getOption("url")
            .flatMap(option -> option.getValue())
            .map(value -> value.asString())
            .orElse(null);

    if (url == null || url.isBlank()) {
      return event.reply("❌ Tu dois fournir une URL valide !").withEphemeral(true);
    }

    return Mono.justOrEmpty(event.getInteraction().getMember())
        .flatMap(Member::getVoiceState)
        .flatMap(VoiceState::getChannel)
        .ofType(VoiceChannel.class)
        .flatMap(channel -> channel.join(spec -> spec.setProvider(provider)))
        .doOnSuccess(ignored -> playerManager.loadItemOrdered(player, url, scheduler))
        .then(event.reply("▶️ Lecture de : " + url));
  }

  @Override
  public List<ApplicationCommandOptionData> getOptions() {
    return List.of(
        ApplicationCommandOptionData.builder()
            .name("url")
            .description("URL de la piste à lire")
            .required(true)
            .build());
  }
}
