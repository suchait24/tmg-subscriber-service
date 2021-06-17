package com.infogain.gcp.poc.consumer.component;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.PubsubMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicPublisher {

    @Value("${app.topic.name}")
    private String topicName;

    public void publishSingleMessage(PubsubMessage message) throws IOException, InterruptedException {

        Publisher publisher = null;

        try {
            publisher = Publisher.newBuilder(topicName).build();
            publisher.publish(message);
            //log.info("message published.");
        } finally {
            if (publisher != null) {
                // When finished with the publisher, shutdown to free up resources.
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
        }

    }
}
