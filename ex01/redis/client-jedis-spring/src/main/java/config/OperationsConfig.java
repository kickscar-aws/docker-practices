package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.*;

/**
 *  convenient bean configuration for template operations
 */
@Configuration
@Import({TemplateConfig.class})
public class OperationsConfig {
    @Bean
    public ValueOperations<?, ?> valueOperations(RedisTemplate<?, ?> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    @Bean
    public HashOperations<?, ?, ?> hashOperations(RedisTemplate<?, ?> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    @Bean
    public ListOperations<?, ?> listOperations(RedisTemplate<?, ?> redisTemplate) {
        return redisTemplate.opsForList();
    }

    @Bean
    public SetOperations<?, ?> setOperations(RedisTemplate<?, ?> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    @Bean
    public ZSetOperations<?, ?> zsetOperations(RedisTemplate<?, ?> redisTemplate) {
        return redisTemplate.opsForZSet();
    }
}
