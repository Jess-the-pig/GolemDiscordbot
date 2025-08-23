package Golem.api.music.play_song;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service permettant d'interagir avec {@code yt-dlp}.
 *
 * <p>- Recherche l'URL d'un flux audio via une requête textuelle. <br>
 * - Vérifie si {@code yt-dlp} est installé et fonctionnel. <br>
 *
 * <p>Les méthodes renvoient des {@link Mono} pour être utilisées de façon réactive dans la pipeline
 * Discord4J.
 */
@Service
public class YoutubeSearchService {
  private static final Logger log = LoggerFactory.getLogger(playCommand.class); // 👈 Logger manuel

  /**
   * Utilise {@code yt-dlp} pour rechercher un flux audio correspondant à une requête.
   *
   * <p>Exemple : "Never Gonna Give You Up" → renvoie l’URL audio directe de la première vidéo
   * trouvée.
   *
   * @param query texte ou URL donné par l’utilisateur
   * @return un {@link Mono} contenant l’URL du flux audio (bestaudio) ou une erreur si non trouvé
   */
  public Mono<String> searchAudioStreamUrl(String query) {
    return Mono.fromCallable(
            () -> {
              ProcessBuilder pb =
                  new ProcessBuilder(
                      "yt-dlp", "-f", "bestaudio", "--get-url", "ytsearch1:" + query);
              pb.redirectErrorStream(true);

              Process process = pb.start();

              try (BufferedReader reader =
                  new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String audioUrl = reader.readLine();
                int exitCode = process.waitFor();

                if (exitCode != 0) {
                  throw new RuntimeException("yt-dlp a échoué avec le code : " + exitCode);
                }

                if (audioUrl == null || audioUrl.isBlank()) {
                  throw new RuntimeException("Aucun flux audio trouvé pour : " + query);
                }

                return audioUrl.trim();
              }
            })
        .doOnSuccess(url -> log.info("✅ URL audio trouvée : " + url))
        .doOnError(err -> log.error("❌ Erreur yt-dlp : ", err));
  }

  /**
   * Vérifie si {@code yt-dlp} est bien installé et accessible depuis l'environnement.
   *
   * @return {@link Mono} contenant {@code true} si yt-dlp est fonctionnel
   */
  public Mono<Boolean> checkYtDlpAvailable() {
    return Mono.fromCallable(
        () -> {
          ProcessBuilder pb = new ProcessBuilder("yt-dlp", "--version");
          Process process = pb.start();
          int exitCode = process.waitFor();
          return exitCode == 0;
        });
  }
}
