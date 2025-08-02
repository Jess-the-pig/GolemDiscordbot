package Golem.api.security;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class RedisStreamConfig {

  private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
  private final DiscordClient discordClient;

  // Instancie le rate limiter ici (ou via @Bean si tu préfères)
  private RedisRateLimiter redisRateLimiter;

  @PostConstruct
  public void init() {
    this.redisRateLimiter = new RedisRateLimiter(reactiveRedisTemplate);
    registerDiscordEventListener();
  }

  @Bean
  public StreamMessageListenerContainer<String, MapRecord<String, String, String>>
      redisStreamListener(RedisConnectionFactory connectionFactory) {

    StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
        StreamMessageListenerContainerOptions.builder().pollTimeout(Duration.ofSeconds(1)).build();

    StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
        StreamMessageListenerContainer.create(connectionFactory, options);

    container.receive(
        org.springframework.data.redis.connection.stream.Consumer.from("myGroup", "myConsumer"),
        StreamOffset.create("discord-events", ReadOffset.lastConsumed()),
        message -> {
          System.out.println("Traitement de l'événement : " + message.getValue());
          reactiveRedisTemplate
              .opsForStream()
              .acknowledge("discord-events", "myGroup", message.getId())
              .subscribe(ack -> System.out.println("Acked: " + ack));
        });

    container.start();
    return container;
  }

  private void registerDiscordEventListener() {
    GatewayDiscordClient gateway = discordClient.login().block();

    gateway
        .on(ChatInputInteractionEvent.class)
        .flatMap(
            event -> {
              String userId = event.getInteraction().getUser().getId().asString();
              return redisRateLimiter
                  .isAllowed("ratelimit:" + userId, 5, 60) // 5 req max / 60 secondes
                  .flatMap(
                      isAllowed -> {
                        if (isAllowed) {
                          Map<String, String> data =
                              Map.of(
                                  "author", event.getInteraction().getUser().getUsername(),
                                  "content", event.getCommandName());
                          return publishToRedisStream("discord-events", data);
                        } else {
                          System.out.println("Too many requests for user " + userId);
                          return Mono.empty();
                        }
                      });
            })
        .subscribe();

    new Thread(() -> gateway.onDisconnect().block()).start();
  }

  private Mono<String> publishToRedisStream(String streamKey, Map<String, String> eventData) {
    return reactiveRedisTemplate
        .opsForStream()
        .add(streamKey, eventData)
        .map(recordId -> recordId.getValue())
        .doOnNext(id -> System.out.println("Added event to stream with id: " + id));
  }
}
