package config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

/**
 * Spring Data Repository for Redis CRUD
 */
@Configuration
@Import({TemplateConfig.class})
@EnableRedisRepositories("repository")
public class RepositoryConfig {
}
