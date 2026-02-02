package com.esd.redis;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisQueueService {

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String, String> redis;

    // ---------- PRODUCER ----------
    public void pushCt(String msg) {
        redis.opsForList().rightPush(RedisKeys.CT_QUEUE, msg);
        System.out.println("REDIS ➕ CT | " + msg);
    }

    public void pushWm(String msg) {
        redis.opsForList().rightPush(RedisKeys.WM_QUEUE, msg);
        System.out.println("REDIS ➕ WM | " + msg);
    }

    // ---------- CONSUMER ----------
    public String popCt() {
        return redis.opsForList()
                .leftPop(RedisKeys.CT_QUEUE, Duration.ofSeconds(5)); // BLOCKING
    }

    public String popWm() {
        return redis.opsForList()
                .leftPop(RedisKeys.WM_QUEUE, Duration.ofSeconds(5)); // BLOCKING
    }

    @PostConstruct
    public void testRedisConnection() {
        redis.opsForValue().set("esd:test", "ok");
        System.out.println("REDIS TEST VALUE = " +
                redis.opsForValue().get("esd:test"));
    }
}
