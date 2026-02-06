package com.esd.redis;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisQueueService {

    private static final Logger log =
            LoggerFactory.getLogger(RedisQueueService.class);

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String, String> redis;

    // ---------- PRODUCER ----------
    public void pushCt(String msg) {
        redis.opsForList().rightPush(RedisKeys.CT_QUEUE, msg);

        if (log.isDebugEnabled()) {
            Long size = redis.opsForList().size(RedisKeys.CT_QUEUE);
            log.debug("REDIS CT push | queueSize={}", size);
        }
    }

    public void pushWm(String msg) {
        redis.opsForList().rightPush(RedisKeys.WM_QUEUE, msg);

        if (log.isDebugEnabled()) {
            Long size = redis.opsForList().size(RedisKeys.WM_QUEUE);
            log.debug("REDIS WM push | queueSize={}", size);
        }
    }

    // ---------- CONSUMER ----------
    public String popCt() {
        String msg = redis.opsForList()
                .leftPop(RedisKeys.CT_QUEUE, Duration.ofSeconds(5)); // BLOCKING

        if (msg != null && log.isDebugEnabled()) {
            Long size = redis.opsForList().size(RedisKeys.CT_QUEUE);
            log.debug("REDIS CT pop | queueSize={}", size);
        }
        return msg;
    }

    public String popWm() {
        String msg = redis.opsForList()
                .leftPop(RedisKeys.WM_QUEUE, Duration.ofSeconds(5)); // BLOCKING

        if (msg != null && log.isDebugEnabled()) {
            Long size = redis.opsForList().size(RedisKeys.WM_QUEUE);
            log.debug("REDIS WM pop | queueSize={}", size);
        }
        return msg;
    }

    // ---------- HEALTH CHECK ----------
    @PostConstruct
    public void testRedisConnection() {
        try {
            redis.opsForValue().set("esd:test", "ok");
            String value = redis.opsForValue().get("esd:test");

            log.info("Redis connection OK | testValue={}", value);
        } catch (Exception e) {
            log.error("Redis connection FAILED", e);
            throw e; // fail fast
        }
    }
}
