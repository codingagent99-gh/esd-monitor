package com.esd.worker;

import com.esd.queue.MessageQueue;
import com.esd.service.EsdService;
import com.esd.util.FileFallbackUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class WorkerService implements CommandLineRunner {

    @Autowired
    private MessageQueue queue;

    @Autowired
    private EsdService service;

    @Override
    public void run(String... args) {

        startCtWorkers(5);   // CT load
        startWmWorkers(15);  // WM load
    }

    private void startCtWorkers(int count) {
        for (int i = 0; i < count; i++) {
            Thread worker = new Thread(() -> processCt(), "CT-Worker-" + i);
            worker.setDaemon(true);
            worker.start();
        }
    }

    private void startWmWorkers(int count) {
        for (int i = 0; i < count; i++) {
            Thread worker = new Thread(() -> processWm(), "WM-Worker-" + i);
            worker.setDaemon(true);
            worker.start();
        }
    }

    private void processCt() {
        while (true) {
            String msg = null;
            try {
                msg = queue.dequeueCT();
                service.process(msg);
            } catch (Exception e) {
                FileFallbackUtil.write(msg);
            }
        }
    }

    private void processWm() {
        while (true) {
            String msg = null;
            try {
                msg = queue.dequeueWM();
                service.process(msg);
            } catch (Exception e) {
                FileFallbackUtil.write(msg);
            }
        }
    }
}
