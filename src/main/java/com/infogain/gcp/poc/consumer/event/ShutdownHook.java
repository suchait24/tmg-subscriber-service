package com.infogain.gcp.poc.consumer.event;

import com.google.cloud.pubsub.v1.Publisher;
import com.infogain.gcp.poc.consumer.component.PubSubPublisher;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/*
    We are using single publisher connection in application
    this class will close this connection when application gets shutdown.
 */

@RequiredArgsConstructor
@Component
public class ShutdownHook implements ApplicationListener<ContextClosedEvent> {

    private final PubSubPublisher pubSubPublisher;

    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {

        if (pubSubPublisher != null) {

            Publisher publisher = pubSubPublisher.getPublisher();
            publisher.shutdown();
            publisher.awaitTermination(1, TimeUnit.MINUTES);
        }
    }
}
