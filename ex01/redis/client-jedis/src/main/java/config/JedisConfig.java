package config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.*;
import redis.clients.jedis.csc.CacheConfig;

import java.time.Duration;

@Configuration
public class JedisConfig {
    @Bean
    public JedisClientConfig jedisClientConfig() {
        return DefaultJedisClientConfig
                .builder()
                // .ssl(true)
                // .sslSocketFactory(sslSocketFactory())
                // .user("default")
                // .password("secret")
                .protocol(RedisProtocol.RESP3)  // To enable client-side caching, specify the RESP3 protocol
                .socketTimeoutMillis(5000)      // set timeout to 5 seconds
                .connectionTimeoutMillis(5000)  // set connection timeout to 5 seconds
                .build();
    }

    @Bean
    public CacheConfig cacheConfig() {
        return CacheConfig.builder().maxSize(1000).build();
    }

    @Bean
    public GenericObjectPoolConfig<?> poolConfig() {
        // Future Deprecated
        // GenericObjectPoolConfig<Jedis> poolConfig = new JedisPoolConfig();
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

    @Bean
    public JedisPooled jedisPooled(JedisClientConfig jedisClientConfig, CacheConfig cacheConfig, GenericObjectPoolConfig<Connection> poolConfig) {
        return new JedisPooled(new HostAndPort("localhost", 6379), jedisClientConfig, cacheConfig, poolConfig);
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

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}