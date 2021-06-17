package com.infogain.gcp.poc.consumer.pubsub;

import com.infogain.gcp.poc.consumer.service.PullSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.gcp.pubsub.core.subscriber.PubSubSubscriberTemplate;
import org.springframework.cloud.gcp.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.cloud.gcp.pubsub.support.converter.ConvertedAcknowledgeablePubsubMessage;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartProcessing {

    private final PubSubSubscriberTemplate subSubscriberTemplate;
    private final PullSubscriptionService pullDemo;

    @PostConstruct
    void runPullAlways() {
        log.info("Start pull mechanism in background.");

        //We can use a thread pool as well rather than using single thread.
       Thread subscriberThread = new Thread(() -> {
            while(true) {
                try {
                    pullDemo.pullMessage(subSubscriberTemplate);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JAXBException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
       });

       subscriberThread.setName("subscriber-background-thread");
       subscriberThread.start();
    }

}
