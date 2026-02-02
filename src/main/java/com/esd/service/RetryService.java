package com.esd.service;

import com.esd.queue.MessageQueue;
import com.esd.redis.RedisQueueService;
import com.esd.util.FileFallbackUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class RetryService {

    @Autowired
    private RedisQueueService redisQueue;


//    @Scheduled(fixedDelay = 30000)
//    public void retry() {
//
//        try {
//            List<String> records = FileFallbackUtil.readAll();
//
//            for (String record : records) {
//
//                if (record.startsWith("CT$$$")) {
//                    queue.enqueueCT(record.substring(5));
//                } else if (record.startsWith("WM$$$")) {
//                    queue.enqueueWM(record.substring(5));
//                }
//            }
//
//            FileFallbackUtil.clear();
//
//        } catch (Exception ignored) {
//        }
//    }

    @Scheduled(fixedDelay = 30000)
    public void retry() throws IOException {

        List<String> records = FileFallbackUtil.readAll();
        List<String> remaining = new ArrayList<>();

        for (String record : records) {
            try {
                String[] parts = record.split("\\|", 2);
                redisQueue.pushCt(parts[1]);
            } catch (Exception e) {
                remaining.add(record);
            }
        }

        FileFallbackUtil.overwrite(remaining);
    }

}
