package com.esd.queue;

import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class MessageQueue {


    private final BlockingQueue<String> ctQueue =
            new LinkedBlockingQueue<>(50000);   // CT safe buffer

    private final BlockingQueue<String> wmQueue =
            new LinkedBlockingQueue<>(100000);  // WM high volume

    // 🔥 NEVER DROP
    public void enqueueCT(String msg) throws InterruptedException {
        ctQueue.put(msg); // blocks instead of dropping
    }

    public void enqueueWM(String msg) throws InterruptedException {
        wmQueue.put(msg);
    }

    public String dequeueCT() throws InterruptedException {
        return ctQueue.take();
    }

    public String dequeueWM() throws InterruptedException {
        return wmQueue.take();
    }


}
