package com.infogain.gcp.poc.consumer.component;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.PubsubMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicPublisher {

    @Value("${app.topic.name}")
    private String topicName;

    private ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

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

    public void publishMessage(List<CompletableFuture<PubsubMessage>> messageList) throws InterruptedException, IOException, JAXBException, ExecutionException {

        Publisher publisher = null;

            // Create a publisher instance with default settings bound to the topic
            publisher = Publisher.newBuilder(topicName).build();

        Publisher finalPublisher = publisher;

        try {

            messageList.stream()
                    .map(CompletableFuture::join)
                    .map(pubsubMessage -> CompletableFuture.supplyAsync(() -> finalPublisher.publish(pubsubMessage), pool));
        } finally {
            if (publisher != null) {
                // When finished with the publisher, shutdown to free up resources.
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
        }

        /*
            try {

                for (CompletableFuture future : messageList) {

                    PubsubMessage message = (PubsubMessage) future.get();

                    // Once published, returns a server-assigned message id (unique within the topic)
                    publisher.publish(message);
                    log.info("message published!");
                }
            } finally {
                if (publisher != null) {
                    // When finished with the publisher, shutdown to free up resources.
                    publisher.shutdown();
                    publisher.awaitTermination(1, TimeUnit.MINUTES);
                }
            }

         */

    }
}
