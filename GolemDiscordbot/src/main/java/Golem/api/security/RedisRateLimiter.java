package Golem.api.security;

import java.time.Duration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Mono;

public class RedisRateLimiter {

  private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

  public RedisRateLimiter(ReactiveRedisTemplate<String, String> reactiveRedisTemplate) {
    this.reactiveRedisTemplate = reactiveRedisTemplate;
  }

  /**
   * Check si un utilisateur est autorisé à faire une requête.
   *
   * @param key clé unique par utilisateur, ex: "ratelimit:userId"
   * @param maxRequests nombre max de requêtes autorisées
   * @param windowSeconds fenêtre temporelle (durée) en secondes
   * @return Mono<Boolean> true si autorisé, false si rate limit atteint
   */
  public Mono<Boolean> isAllowed(String key, int maxRequests, int windowSeconds) {
    // Incrémente la clé atomiquement
    return reactiveRedisTemplate
        .opsForValue()
        .increment(key)
        .flatMap(
            count -> {
              if (count == 1) {
                // Première requête → set expiration
                return reactiveRedisTemplate
                    .expire(key, Duration.ofSeconds(windowSeconds))
                    .map(expired -> true);
              } else {
                // Si on dépasse maxRequests → false sinon true
                return Mono.just(count <= maxRequests);
              }
            });
  }
}
