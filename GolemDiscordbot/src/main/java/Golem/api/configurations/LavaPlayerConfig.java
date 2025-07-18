package Golem.api.configurations;

import Golem.api.services.LavaPlayerAudioProvider;
import Golem.api.services.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LavaPlayerConfig {

  @Bean
  public AudioPlayer audioPlayer(AudioPlayerManager manager) {
    return manager.createPlayer();
  }

  @Bean
  public LavaPlayerAudioProvider lavaPlayerAudioProvider(AudioPlayer player) {
    return new LavaPlayerAudioProvider(player);
  }

  @Bean
  public TrackScheduler trackScheduler(AudioPlayer player) {
    TrackScheduler scheduler = new TrackScheduler(player);
    player.addListener(scheduler);
    return scheduler;
  }

  @Bean
  public AudioPlayerManager audioPlayerManager() {
    DefaultAudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    playerManager.registerSourceManager(new YoutubeAudioSourceManager());
    playerManager.registerSourceManager(new BandcampAudioSourceManager());
    playerManager.registerSourceManager(new VimeoAudioSourceManager());
    playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
    playerManager.registerSourceManager(new LocalAudioSourceManager());
    return playerManager;
  }
}
