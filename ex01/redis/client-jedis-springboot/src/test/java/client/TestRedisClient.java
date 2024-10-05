package client;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestRedisClient {
    @Autowired
    RedisConnectionFactory redisConnectionFactory;


//    @Resource(name="valueOperations")
//    private ValueOperations<String, Object> valueOperations;
//
//    @Resource(name="hashOperations")
//    private HashOperations<String, String, Object> hashOperations;
//
//    @Resource(name="listOperations")
//    private ListOperations<String, Object> listOperations;
//
//    @Resource(name="setOperations")
//    private SetOperations<String, Object> setOperations;
//
//    @Resource(name="zsetOperations")
//    private ZSetOperations<String, Object> zsetOperations;

    @Test
    @Order(1)
    public void test() {
        assertNotNull(redisConnectionFactory);
        log.info("redisConnectionFactory={}", redisConnectionFactory);
    }

}