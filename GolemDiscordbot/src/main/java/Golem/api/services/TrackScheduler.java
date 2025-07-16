package Golem.api.services;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import java.util.LinkedList;
import java.util.Queue;
import org.springframework.stereotype.Component;

@Component
public class TrackScheduler extends AudioEventAdapter {
  private final AudioPlayer player;
  private final Queue<AudioTrack> queue;

  public TrackScheduler(AudioPlayer player) {
    this.player = player;
    this.queue = new LinkedList<>();
  }

  public void queue(AudioTrack track) {
    if (!player.startTrack(track, true)) {
      queue.offer(track);
    }
  }

  @Override
  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
    if (endReason.mayStartNext) {
      nextTrack();
    }
  }

  private void nextTrack() {
    player.startTrack(queue.poll(), false);
  }
}
