package com.esd.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class MessageQueue {

    private static final Logger log =
            LoggerFactory.getLogger(MessageQueue.class);

    private static final int CT_WARN_THRESHOLD = 40000;
    private static final int WM_WARN_THRESHOLD = 80000;

    private final BlockingQueue<String> ctQueue =
            new LinkedBlockingQueue<>(50000);   // CT safe buffer

    private final BlockingQueue<String> wmQueue =
            new LinkedBlockingQueue<>(100000);  // WM high volume

    // ---------- CT QUEUE ----------
    public void enqueueCT(String msg) throws InterruptedException {

        int sizeBefore = ctQueue.size();
        ctQueue.put(msg);   // blocks instead of dropping
        int sizeAfter = ctQueue.size();

        if (sizeAfter > CT_WARN_THRESHOLD) {
            log.warn("CT queue high load | size={}", sizeAfter);
        } else if (log.isDebugEnabled()) {
            log.debug("CT message enqueued | size={}", sizeAfter);
        }
    }

    public String dequeueCT() throws InterruptedException {

        String msg = ctQueue.take();
        int size = ctQueue.size();

        if (log.isDebugEnabled()) {
            log.debug("CT message dequeued | size={}", size);
        }
        return msg;
    }

    // ---------- WM QUEUE ----------
    public void enqueueWM(String msg) throws InterruptedException {

        int sizeBefore = wmQueue.size();
        wmQueue.put(msg);
        int sizeAfter = wmQueue.size();

        if (sizeAfter > WM_WARN_THRESHOLD) {
            log.warn("WM queue high load | size={}", sizeAfter);
        } else if (log.isDebugEnabled()) {
            log.debug("WM message enqueued | size={}", sizeAfter);
        }
    }

    public String dequeueWM() throws InterruptedException {

        String msg = wmQueue.take();
        int size = wmQueue.size();

        if (log.isDebugEnabled()) {
            log.debug("WM message dequeued | size={}", size);
        }
        return msg;
    }
}
