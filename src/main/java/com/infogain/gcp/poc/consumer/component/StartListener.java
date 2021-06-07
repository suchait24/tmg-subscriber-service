/*
package com.infogain.gcp.poc.consumer.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartListener {

    private final MessageSubscriber messageSubscriber;

    @PostConstruct
    public void startListening() {

        log.info("startListening has been started in background thread.");
        new Thread(() -> messageSubscriber.createSubscription()).start();
    }

}
*/