package Golem.api.services;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public final class TrackScheduler implements AudioLoadResultHandler {

  private final AudioPlayer player;

  public TrackScheduler(final AudioPlayer player) {
    this.player = player;
  }

  @Override
  public void trackLoaded(final AudioTrack track) {
    player.playTrack(track);
  }

  @Override
  public void playlistLoaded(final AudioPlaylist playlist) {
    // Optionnel : joue le premier titre de la playlist
    if (!playlist.getTracks().isEmpty()) {
      player.playTrack(playlist.getTracks().get(0));
    }
  }

  @Override
  public void noMatches() {
    System.out.println("Aucun résultat trouvé !");
  }

  @Override
  public void loadFailed(final FriendlyException exception) {
    System.out.println("Échec du chargement : " + exception.getMessage());
  }
}
