package com.esd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadConfig {

    @Bean("ctExecutor")
    public ExecutorService ctExecutor() {
        return Executors.newFixedThreadPool(5);   // CT = safety critical
    }

    @Bean("wmExecutor")
    public ExecutorService wmExecutor() {
        return Executors.newFixedThreadPool(15);  // WM = high volume
    }

    @Bean("tcpExecutor")
    public ExecutorService tcpExecutor() {
        return Executors.newFixedThreadPool(50);  // sockets
    }
}
