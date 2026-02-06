package com.esd.worker;

import com.esd.redis.RedisQueueService;
import com.esd.service.EsdService;
import com.esd.util.FileFallbackUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class WmWorker {

    private static final Logger log =
            LoggerFactory.getLogger(WmWorker.class);

    private static final int WORKER_COUNT = 15;

    @Autowired
    private RedisQueueService redisQueue;

    @Autowired
    private EsdService service;

    @Autowired
    @Qualifier("wmExecutor")
    private ExecutorService executor;

    @PostConstruct
    public void start() {

        for (int i = 0; i < WORKER_COUNT; i++) {
            int workerId = i;
            executor.submit(() -> process(workerId));
        }

        log.info("WM workers started | count={}", WORKER_COUNT);
    }

    private void process(int workerId) {

        log.info("WM worker running | workerId={}", workerId);

        while (true) {
            String msg = null;

            try {
                msg = redisQueue.popWm();

                if (msg == null) {
                    Thread.sleep(500);
                    continue;
                }

                if (log.isDebugEnabled()) {
                    log.debug(
                            "WM worker processing message | workerId={} length={} hash={}",
                            workerId,
                            msg.length(),
                            msg.hashCode()
                    );
                }

                service.process(msg);

            } catch (Exception e) {

                log.error(
                        "WM worker failed | workerId={} length={} hash={}",
                        workerId,
                        msg != null ? msg.length() : 0,
                        msg != null ? msg.hashCode() : 0,
                        e
                );

                if (msg != null) {
                    FileFallbackUtil.write(msg);
                }
            }
        }
    }
}
