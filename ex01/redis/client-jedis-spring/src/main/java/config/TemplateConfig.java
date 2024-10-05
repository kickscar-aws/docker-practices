package config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import redis.clients.jedis.Connection;
import redis.clients.jedis.ConnectionPoolConfig;
import java.time.Duration;

@Configuration
public class TemplateConfig {
    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new GenericToStringSerializer<Object>(Object.class) /* new JdkSerializationRedisSerializer() */);
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer() /* Default, Java Native Serialization, Not Recommended */);
        redisTemplate.setHashKeySerializer(new GenericToStringSerializer<Object>(Object.class));
        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());

        return redisTemplate;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory(JedisClientConfiguration jedisClientConfiguration) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration("localhost", 6379);
        // redisStandaloneConfiguration.getUsername("default");
        // redisStandaloneConfiguration.setPassword(RedisPassword.of("secret"));

        return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
    }

    @Bean
    public JedisClientConfiguration jedisClientConfiguration(GenericObjectPoolConfig<?> poolConfig /* , SSLSocketFactory sslSocketFactory */) {
        return JedisClientConfiguration
                .builder()

                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(5))

                .usePooling()
                .poolConfig(poolConfig)

                .and()
                // .useSsl()
                // .sslSocketFactory(sslSocketFactory())

                .build();
    }

    @Bean
    public GenericObjectPoolConfig<?> poolConfig() {
        GenericObjectPoolConfig<Connection> poolConfig = new ConnectionPoolConfig();

        // maximum active connections in the pool, tune this according to your needs and application type default is 8
        poolConfig.setMaxTotal(8);

        // maximum idle connections in the pool, default is 8
        poolConfig.setMaxIdle(8);

        // minimum idle connections in the pool, default 0
        poolConfig.setMinIdle(0);

        // Enables waiting for a connection to become available.
        poolConfig.setBlockWhenExhausted(true);

        // The maximum number of seconds to wait for a connection to become available
        poolConfig.setMaxWait(Duration.ofSeconds(1));

        // Enables sending a PING command periodically while the connection is idle.
        poolConfig.setTestWhileIdle(true);

        // controls the period between checks for idle connections in the pool, Default 30s
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofSeconds(30));

        //
        poolConfig.setMinEvictableIdleDuration(Duration.ofSeconds(60)); // Default 60s

        // Test Properties
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setNumTestsPerEvictionRun(3); // Default -1

        return poolConfig;
    }

//    @Bean
//    public SSLSocketFactory sslSocketFactory() throws Throwable {
//        final String caCertPath = "./truststore.jks";
//        final String caCertPassword = "secret";
//        final String userCertPath = "./redis-user-keystore.p12";
//        final String userCertPassword = "secret";
//
//        KeyStore keyStore = KeyStore.getInstance("pkcs12");
//        keyStore.load(new FileInputStream(userCertPath), userCertPassword.toCharArray());
//
//        KeyStore trustStore = KeyStore.getInstance("jks");
//        trustStore.load(new FileInputStream(caCertPath), caCertPassword.toCharArray());
//
//        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("X509");
//        trustManagerFactory.init(trustStore);
//
//        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("PKIX");
//        keyManagerFactory.init(keyStore, userCertPassword.toCharArray());
//
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
//
//        return sslContext.getSocketFactory();
//    }
}