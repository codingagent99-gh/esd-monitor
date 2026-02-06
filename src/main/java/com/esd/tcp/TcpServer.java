package com.esd.tcp;

import com.esd.queue.MessageQueue;
import com.esd.redis.RedisQueueService;
import com.esd.util.FileFallbackUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

@Component
public class TcpServer {

    private static final Logger log =
            LoggerFactory.getLogger(TcpServer.class);

    @Autowired
    private RedisQueueService redisQueue;

    @Autowired
    private MessageQueue queue; // (kept if used later)

    @Autowired
    @Qualifier("tcpExecutor")
    private ExecutorService tcpExecutor;

    @PostConstruct
    public void start() {
        new Thread(this::runServer, "tcp-server-thread").start();
        log.info("TCP Server thread started");
    }

    private void runServer() {

        try (ServerSocket serverSocket = new ServerSocket(9000)) {

            log.info("TCP Server listening on port 9000");

            while (true) {
                Socket socket = serverSocket.accept();

                log.info(
                        "TCP client connected | remoteAddress={}",
                        socket.getRemoteSocketAddress()
                );

                tcpExecutor.submit(() -> handleClient(socket));
            }

        } catch (Exception e) {
            log.error("TCP Server failed", e);
        }
    }

    private void handleClient(Socket socket) {

        String client = socket.getRemoteSocketAddress().toString();

        try (BufferedReader br =
                     new BufferedReader(
                             new InputStreamReader(socket.getInputStream()))) {

            String line;
            long ctCount = 0;
            long wmCount = 0;

            while ((line = br.readLine()) != null) {

                // Ignore noise
                if (!line.startsWith("$$$$")) {
                    continue;
                }

                String[] p = line.split(",");

                if (p.length < 2) {
                    log.warn(
                            "Malformed TCP message skipped | client={}",
                            client
                    );
                    continue;
                }

                if ("CT".equals(p[1])) {

                    // Write-ahead log first
                    FileFallbackUtil.write("CT|" + line);
                    redisQueue.pushCt(line);
                    ctCount++;

                } else if ("WM".equals(p[1])) {

                    redisQueue.pushWm(line);
                    wmCount++;
                }
            }

            log.info(
                    "TCP client disconnected | client={} ctCount={} wmCount={}",
                    client,
                    ctCount,
                    wmCount
            );

        } catch (Exception e) {

            log.error(
                    "TCP client handler failed | client={}",
                    client,
                    e
            );
        }
    }
}
