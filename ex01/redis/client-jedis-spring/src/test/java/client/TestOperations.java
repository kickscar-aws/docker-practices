package client;

import config.OperationsConfig;
import domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(classes = {OperationsConfig.class})
class TestOperations {
    @Resource(name="valueOperations")
    private ValueOperations<String, Object> valueOperations;

    @Resource(name="hashOperations")
    private HashOperations<String, String, Object> hashOperations;

    @Resource(name="listOperations")
    private ListOperations<String, Object> listOperations;

    @Resource(name="setOperations")
    private SetOperations<String, Object> setOperations;

    @Resource(name="zsetOperations")
    private ZSetOperations<String, Object> zsetOperations;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    @Order(1)
    public void testStringValue() { /* set, get */
        valueOperations.set("message", "Hello, World");
        assertEquals("Hello, World", valueOperations.get("message"));
    }

    @Test
    @Order(2)
    public void testJavaStandardSerialization() { /* set, get */
        valueOperations.set("user01", new User("ddochi", "ddochi@gmail.com", "male", 10));
        assertEquals(new User("ddochi", "ddochi@gmail.com", "male", 10), valueOperations.get("user01"));
    }

    @Test
    @Order(3)
    public void testJsonSerialization() { /* set, get */
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        valueOperations.set("user02", new User("donut", "donut@gmail.com", "male", 10));
        assertEquals(new User("donut", "donut@gmail.com", "male", 10), valueOperations.get("user02"));
    }

    @Test
    @Order(4)
    public void testHash() { /* hset & hmset, hget & hgetall */
        hashOperations.put("user03", "name", "dooly");
        hashOperations.put("user03", "email", "dooly@gmail.com");
        hashOperations.put("user03", "gender", "male");
        hashOperations.putAll("user04", Map.of("name", "michol", "email", "michol@gmail.com", "gender", "male"));

        assertEquals("dooly", hashOperations.get("user03", "name"));
        assertEquals("dooly@gmail.com", hashOperations.get("user03", "email"));
        assertEquals("male", hashOperations.get("user03", "gender"));
        assertEquals(Map.of("name", "dooly", "email", "dooly@gmail.com", "gender", "male"), hashOperations.entries("user03"));
        assertEquals(Map.of("name", "michol", "email", "michol@gmail.com", "gender", "male"), hashOperations.entries("user04"));
    }

    @Test
    @Order(5)
    public void testList() { /* rpush & lpush, rpop & lpop & lrange */
        assertEquals(1L /* the count of elements */, listOperations.rightPush("musics", "Pathetique"));
        assertEquals(3L /* the count of elements */, listOperations.rightPushAll("musics", "Nocturnes", "Tempest"));
        assertEquals(4L /* the count of elements */, listOperations.leftPush("musics", "Moonlight"));
        assertEquals(6L /* the count of elements */, listOperations.leftPushAll("musics", "Etudes", "Polonez"));

        assertEquals("Tempest", listOperations.rightPop("musics"));
        assertEquals("Polonez", listOperations.leftPop("musics"));
        assertEquals(List.of("Etudes", "Moonlight", "Pathetique", "Nocturnes"), listOperations.range("musics", 0, -1));
    }

    @Test
    @Order(6)
    public void testSet() { /* sadd, sismember, smembers */
        assertEquals(1L, setOperations.add("friends", "dooly"));
        assertEquals(1L, setOperations.add("friends", "michol"));
        assertEquals(1L, setOperations.add("friends", "ddochi"));
        assertEquals(0L, setOperations.add("friends", "dooly"));

        assertEquals(Boolean.TRUE, setOperations.isMember("friends", "michol"));
        assertEquals(Boolean.FALSE, setOperations.isMember("friends", "loopy"));

        assertEquals(Set.of("ddochi", "michol", "dooly"), setOperations.members("friends"));
    }

    @Test
    @Order(7)
    public void testSortedSet() { /* zadd, zrevrange, zrevrank */
        assertEquals(Boolean.TRUE, zsetOperations.add("heights", "dooly", 140));
        assertEquals(Boolean.TRUE, zsetOperations.add("heights", "michol", 181));
        assertEquals(Boolean.TRUE, zsetOperations.add("heights", "ddochi", 154));
        assertEquals(Boolean.FALSE, zsetOperations.add("heights", "dooly", 155));
        assertEquals(Boolean.TRUE, zsetOperations.add("heights", "loopy", 130));
        assertEquals(Boolean.TRUE, zsetOperations.add("heights", "donut", 148));

        assertEquals(Set.of("michol", "dooly", "ddochi"), zsetOperations.reverseRange("heights", 0, 2));
        assertEquals(4L, zsetOperations.reverseRank("heights", "loopy"));
    }

    @Disabled
    @Order(9)
    @Test
    public void testCache() {
        IntStream.range(0, 10).boxed().toList().forEach(Consumer.THROWS(n -> System.out.println( valueOperations.get("user04"))));
    }

    @Test
    @Order(10)
    public void testDelete() {
        assertEquals(Boolean.TRUE, valueOperations.getOperations().delete("message"));
        assertEquals(Boolean.TRUE, valueOperations.getOperations().delete("user01"));
        assertEquals(Boolean.TRUE, valueOperations.getOperations().delete("user02"));
        assertEquals(Boolean.TRUE, hashOperations.getOperations().delete("user03"));
        assertEquals(Boolean.TRUE, hashOperations.getOperations().delete("user04"));
        assertEquals(Boolean.TRUE, listOperations.getOperations().delete("musics"));
        assertEquals(Boolean.TRUE, setOperations.getOperations().delete("friends"));
        assertEquals(Boolean.TRUE, zsetOperations.getOperations().delete("heights"));
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