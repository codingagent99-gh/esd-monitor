package com.esd.worker;

import com.esd.queue.MessageQueue;
import com.esd.redis.RedisQueueService;
import com.esd.service.EsdService;
import com.esd.util.FileFallbackUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class WmWorker {

    @Autowired
    private RedisQueueService redisQueue;

    @Autowired
    private EsdService service;

    @Autowired
    @Qualifier("wmExecutor")
    private ExecutorService executor;

    @PostConstruct
    public void start() {
        for (int i = 0; i < 15; i++) {
            executor.submit(this::process);
        }
        System.out.println("✅ WM workers started");
    }

    private void process() {
        while (true) {
            String msg = null;
            try {
                msg = redisQueue.popWm();
                if (msg == null) {
                    Thread.sleep(500);
                    continue;
                }

                System.out.println("WM WORKER ⏩ " + msg);

                service.process(msg);
            } catch (Exception e) {
                if (msg != null) {
                    FileFallbackUtil.write(msg);
                }
            }
        }
    }
}
