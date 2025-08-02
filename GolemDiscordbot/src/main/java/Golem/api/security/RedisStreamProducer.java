package Golem.api.security;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisStreamProducer {
  @Autowired private StringRedisTemplate redisTemplate;

  public String publishEvent(String streamKey, Map<String, String> message) {
    StreamOperations<String, Object, Object> streamOps = redisTemplate.opsForStream();
    return streamOps.add(streamKey, message).toString();
  }
}
