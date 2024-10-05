package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.JedisConfig;
import domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.SerializationUtils;
import redis.clients.jedis.JedisPooled;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(classes = {JedisConfig.class})
class TestJedisClient {
    @Autowired
    JedisPooled jedis;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @Order(1)
    public void testStringValue() { /* set, get */

        // Not Recommended
        // try (JedisPool pool = new JedisPool("localhost", 6379)) {
        //    try (Jedis jedis = pool.getResource()) {
        //        assertEquals("OK", jedis.set("message", "Hello, World"));
        //    }
        // }

        assertEquals("OK", jedis.set("message", "Hello, World"));
        assertEquals("Hello, World", jedis.get("message"));
    }

    @Test
    @Order(2)
    public void testBytesValue() { /* set, get */
        assertEquals("OK", jedis.set("user01".getBytes(), SerializationUtils.serialize(new User("ddochi", "ddochi@gmail.com", "male", 10))));

        // Deprecated:
        // SerializationUtils.deserialize: Remote Code Execution (RCE) vulnerabilities
        // Prefer the use of an external tool that serializes to JSON, XML, or any other format
        assertEquals(new User("ddochi", "ddochi@gmail.com", "male", 10), SerializationUtils.deserialize(jedis.get("user01".getBytes())));
    }

    @Test
    @Order(3)
    public void testJson() throws Throwable { /* set, get */
        assertEquals("OK", jedis.set("user02", objectMapper.writeValueAsString(new User("donut", "donut@gmail.com", "male", 10))));
        assertEquals(new User("donut", "donut@gmail.com", "male", 10), objectMapper.readValue(jedis.get("user02"), User.class));
    }

    @Test
    @Order(4)
    public void testHash() { /* hset & hmset, hget & hgetall */
        assertEquals(1L /* the number of fields */, jedis.hset("user03", "name", "dooly"));
        assertEquals(1L /* the number of fields */, jedis.hset("user03", "email", "dooly@gmail.com"));
        assertEquals(1L /* the number of fields */, jedis.hset("user03", "gender", "male"));
        // assertEquals(3L /* the number of fields */, jedis.hset("user04", Map.of("name", "michol", "email", "michol@gmail.com", "gender", "male")));
        assertEquals("OK", jedis.hmset("user04", Map.of("name", "michol", "email", "michol@gmail.com", "gender", "male")));

        assertEquals("dooly", jedis.hget("user03", "name"));
        assertEquals("dooly@gmail.com", jedis.hget("user03", "email"));
        assertEquals("male", jedis.hget("user03", "gender"));
        assertEquals(Map.of("name", "dooly", "email", "dooly@gmail.com", "gender", "male"), jedis.hgetAll("user03"));
        assertEquals(Map.of("name", "michol", "email", "michol@gmail.com", "gender", "male"), jedis.hgetAll("user04"));
    }

    @Test
    @Order(5)
    public void testList() { /* rpush & lpush, rpop & lpop & lrange */
        assertEquals(1L /* the count of elements */, jedis.rpush("musics", "Pathetique"));
        assertEquals(3L /* the count of elements */, jedis.rpush("musics", "Nocturnes", "Tempest"));
        assertEquals(4L /* the count of elements */, jedis.lpush("musics", "Moonlight"));
        assertEquals(6L /* the count of elements */, jedis.lpush("musics", "Etudes", "Polonez"));

        assertEquals("Tempest", jedis.rpop("musics"));
        assertEquals("Polonez", jedis.lpop("musics"));
        assertEquals(List.of("Etudes", "Moonlight", "Pathetique", "Nocturnes"), jedis.lrange("musics", 0, -1));
    }

    @Test
    @Order(6)
    public void testSet() { /* sadd, sismember, smembers */
        assertEquals(1L, jedis.sadd("friends", "dooly"));
        assertEquals(1L, jedis.sadd("friends", "michol"));
        assertEquals(1L, jedis.sadd("friends", "ddochi"));
        assertEquals(0L, jedis.sadd("friends", "dooly"));

        assertTrue(jedis.sismember("friends", "michol"));
        assertFalse(jedis.sismember("friends", "loopy"));

        assertEquals(Set.of("ddochi", "michol", "dooly"), jedis.smembers("friends"));
    }

    @Test
    @Order(7)
    public void testSortedSet() { /* zadd, zrevrange, zrevrank */
        assertEquals(1L, jedis.zadd("heights", 140, "dooly"));
        assertEquals(1L, jedis.zadd("heights", 181, "michol"));
        assertEquals(1L, jedis.zadd("heights", 154, "ddochi"));
        assertEquals(0L, jedis.zadd("heights", 155, "dooly"));
        assertEquals(1L, jedis.zadd("heights", 130, "loopy"));
        assertEquals(1L, jedis.zadd("heights", 148, "donut"));

        assertEquals(List.of("michol", "dooly", "ddochi"), jedis.zrevrange("heights", 0, 2));
        assertEquals(4L, jedis.zrevrank("heights", "loopy"));
    }

    @Test
    @Order(9)
    public void testCache() {
        // You can see the cache working if you connect to the same Redis database with redis-cli and run the MONITOR command
        // $ redis-cli monitor
        //

        IntStream.range(0, 10).boxed().toList().forEach(Consumer.THROWS(n -> System.out.println(objectMapper.readValue(jedis.get("user02"), User.class))));

        // 1. removed individual keys
        jedis.getCache().deleteByRedisKey("user02");
        // 2. clear all cached
        // jedis.getCache().flush();

        IntStream.range(0, 10).boxed().toList().forEach(Consumer.THROWS(n -> System.out.println(objectMapper.readValue(jedis.get("user02"), User.class))));
    }

    @Test
    @Order(10)
    void testDelete() {
        assertEquals(1L, jedis.del("message"));
        assertEquals(1L, jedis.del("user01".getBytes()));
        assertEquals(1L, jedis.del("user02"));
        assertEquals(1L, jedis.del("user03"));
        assertEquals(1L, jedis.del("user04"));
        assertEquals(1L, jedis.del("musics"));
        assertEquals(1L, jedis.del("friends"));
        assertEquals(1L, jedis.del("heights"));
    }

    // Wrapper for Handling Lambda Exception
    private static class Consumer {
        public static <T> java.util.function.Consumer<T> THROWS(ThrowsProxy<T, Exception> throwsProxy) {
            return t -> {
                try {
                    throwsProxy.accept(t);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            };
        }

        @FunctionalInterface
        public interface ThrowsProxy<T, E extends Exception> {
            void accept(T t) throws E;
        }
    }
}