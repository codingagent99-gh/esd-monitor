package com.esd.tcp;

import com.esd.queue.MessageQueue;
import com.esd.redis.RedisQueueService;
import com.esd.util.FileFallbackUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

@Component

public class TcpServer {

    @Autowired

    private RedisQueueService redisQueue;

    @Autowired
    @Qualifier("tcpExecutor")
    private ExecutorService tcpExecutor;
    @PostConstruct
    public void start() {
        new Thread(this::runServer).start();
        System.out.println("🔥 TCP Server thread started");
    }

    private void runServer() {
        try (ServerSocket serverSocket = new ServerSocket(9000)) {
            System.out.println("TCP Server started on port 9000");

            while (true) {
                Socket socket = serverSocket.accept();
                tcpExecutor.submit(() -> handleClient(socket));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private MessageQueue queue;

    private void handleClient(Socket socket) {

        try (BufferedReader br =
                     new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String line;
            while ((line = br.readLine()) != null) {

                if (!line.startsWith("$$$$")) continue;

                String[] p = line.split(",");

                if ("CT".equals(p[1])) {
                    FileFallbackUtil.write("CT|" + line);  // WAL first
                    redisQueue.pushCt(line);
                } else if ("WM".equals(p[1])) {
                    redisQueue.pushWm(line);               // WM skips WAL
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
