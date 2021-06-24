package com.infogain.gcp.poc.consumer.component;

import com.google.pubsub.v1.PubsubMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicPublisher {

    private final PubSubPublisher pubSubPublisher;

    public void publishSingleMessage(PubsubMessage message) throws IOException, InterruptedException {
        pubSubPublisher.getPublisher().publish(message);
    }
}
