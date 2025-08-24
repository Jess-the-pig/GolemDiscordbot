package Golem.api.music;

import Golem.api.common.interfaces.TimeStampedEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Représente une playlist musicale persistée en base de données.
 *
 * <p>Chaque playlist est associée à un utilisateur via {@code userplaylist} et contient un ensemble
 * d'URL (ex. YouTube, SoundCloud...) qui seront utilisées pour streamer la musique.
 *
 * <p>Les champs {@code dateCreated} et {@code lastUpdated} sont gérés grâce à l'interface {@link
 * TimeStampedEntity} afin de suivre l'historique de création et de mise à jour.
 */
@Entity
@Getter
@Setter
public class Playlist implements TimeStampedEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private LocalDateTime dateCreated;
  private LocalDateTime lastUpdated;

  private List<String> url;
  private Long userplaylist;
  private Long userId;

  public void setUserPlaylist(Long userId) {
    this.userplaylist = userId;
  }

  @Override
  public void setDateCreated(LocalDateTime date) {
    this.dateCreated = date;
  }

  @Override
  public void setLastUpdated(LocalDateTime date) {
    this.lastUpdated = date;
  }
}
