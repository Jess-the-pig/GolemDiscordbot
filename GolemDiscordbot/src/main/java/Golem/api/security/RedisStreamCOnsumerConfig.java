package Golem.api.security;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;

@Configuration
public class RedisStreamCOnsumerConfig {

  @Autowired private RedisConnectionFactory connectionFactory;

  @Bean
  public StreamMessageListenerContainer<String, MapRecord<String, String, String>>
      streamMessageListenerContainer() {
    StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
        StreamMessageListenerContainerOptions.builder().pollTimeout(Duration.ofSeconds(1)).build();

    StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
        StreamMessageListenerContainer.create(connectionFactory, options);

    container.receive(
        StreamOffset.fromStart("discord-events"),
        (message) -> {
          System.out.println("Received from Redis stream: " + message);
          // Ici tu peux traiter ton message : parser, déclencher des actions, etc.
        });

    container.start();
    return container;
  }
}
