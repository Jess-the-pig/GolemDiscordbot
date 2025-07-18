package Golem.api.services;

import Golem.api.commands.implementation.playCommand;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class YoutubeSearchService {
  private static final Logger log = LoggerFactory.getLogger(playCommand.class); // 👈 Logger manuel

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
