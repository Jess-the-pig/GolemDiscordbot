package Golem.api.services;

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import discord4j.voice.AudioProvider;
import java.nio.ByteBuffer;
import org.springframework.context.annotation.Configuration;

@Configuration
public final class LavaPlayerAudioProvider extends AudioProvider {

  private final AudioPlayer audioPlayer;

  public LavaPlayerAudioProvider(AudioPlayer audioPlayer) {
    super(ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize()));
    this.audioPlayer = audioPlayer;
  }

  @Override
  public boolean provide() {
    AudioFrame frame = audioPlayer.provide();
    if (frame != null) {
      getBuffer().clear();
      getBuffer().put(frame.getData());
      return true;
    }
    return false;
  }
}
