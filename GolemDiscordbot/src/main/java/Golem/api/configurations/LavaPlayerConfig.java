package Golem.api.configurations;

import Golem.api.services.LavaPlayerAudioProvider;
import Golem.api.services.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
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
    AudioPlayerManager manager = new DefaultAudioPlayerManager();
    AudioSourceManagers.registerRemoteSources(manager);
    return manager;
  }
}
