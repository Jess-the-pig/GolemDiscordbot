package Golem.api.db;

import Golem.api.music.Playlist;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository pour gérer les {@link Playlist} dans la base de données. Fournit des opérations CRUD
 * via {@link JpaRepository} et des requêtes personnalisées.
 */
@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
  /**
   * Récupère la liste des playlists associées à un utilisateur spécifique.
   *
   * @param userId l'identifiant de l'utilisateur
   * @return la liste des playlists appartenant à l'utilisateur
   */
  public List<Playlist> findByUserId(Long userId);

  /**
   * Récupère la liste des playlists associées à un utilisateur spécifique.
   *
   * @param userId l'identifiant de l'utilisateur
   * @return la liste des playlists appartenant à l'utilisateur
   */
  public Playlist findByName(String name);
}
