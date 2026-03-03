package org.example;

import org.example.infrastructure.redis.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    @Test
    public void testBasicKeyValueOperations() {
        String key = "test:basic:key";
        String value = "test-value";
        
        redisService.set(key, value);
        String result = redisService.get(key);
        assertEquals(value, result);
        
        assertTrue(redisService.exists(key));
        
        redisService.delete(key);
        assertFalse(redisService.exists(key));
    }

    @Test
    public void testKeyValueWithExpiration() throws InterruptedException {
        String key = "test:expire:key";
        String value = "expire-test";
        
        redisService.set(key, value, Duration.ofSeconds(2));
        assertTrue(redisService.exists(key));
        
        long ttl = redisService.getTimeToLive(key);
        assertTrue(ttl > 0);
        
        Thread.sleep(3000);
        assertFalse(redisService.exists(key));
    }

    @Test
    public void testHashOperations() {
        String hashKey = "test:hash:key";
        String field = "test-field";
        String value = "hash-value";
        
        redisService.hSet(hashKey, field, value);
        String result = redisService.hGet(hashKey, field);
        assertEquals(value, result);
        
        assertTrue(redisService.hExists(hashKey, field));
        
        Map<String, String> all = redisService.hGetAll(hashKey);
        assertTrue(all.containsKey(field));
        assertEquals(value, all.get(field));
        
        assertTrue(redisService.hDelete(hashKey, field));
        assertFalse(redisService.hExists(hashKey, field));
    }

    @Test
    public void testListOperations() {
        String listKey = "test:list:key";
        String value1 = "item-1";
        String value2 = "item-2";
        
        redisService.listRightPush(listKey, value1);
        redisService.listRightPush(listKey, value2);
        
        var list = redisService.listGetAll(listKey);
        assertEquals(2, list.size());
        assertEquals(value1, list.get(0));
        assertEquals(value2, list.get(1));
    }

    @Test
    public void testAtomicCounter() {
        String counterKey = "test:counter:key";
        
        long initial = redisService.getAtomicLong(counterKey).get();
        assertEquals(0, initial);
        
        long incremented = redisService.increment(counterKey);
        assertEquals(1, incremented);
        
        long decremented = redisService.decrement(counterKey);
        assertEquals(0, decremented);
    }

    @Test
    public void testDistributedLock() {
        String lockKey = "test:lock:key";
        
        assertTrue(redisService.tryLock(lockKey, 5000, 3000, java.util.concurrent.TimeUnit.MILLISECONDS));
        
        redisService.unlock(lockKey);
    }

    @Test
    public void testFindKeysByPattern() {
        String prefix = "test:pattern:";
        String key1 = prefix + "key1";
        String key2 = prefix + "key2";
        
        redisService.set(key1, "value1");
        redisService.set(key2, "value2");
        
        Iterable<String> keys = redisService.findKeysByPattern(prefix + "*");
        int count = 0;
        for (String ignored : keys) {
            count++;
        }
        assertTrue(count >= 2);
        
        long deleted = redisService.deleteByPattern(prefix + "*");
        assertTrue(deleted >= 2);
    }
}
