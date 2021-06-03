package com.infogain.gcp.poc.consumer.component;

import com.google.api.core.ApiService.Listener;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import com.infogain.gcp.poc.consumer.service.TeleTypeService;
import com.infogain.gcp.poc.consumer.service.impl.TeleTypeServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageSubscriber {

    private static final Integer THREAD_COUNT = 4;
    @Value("${spring.cloud.gcp.project-id}")
    private String projectId;
    @Value("${app.subscription.id}")
    private String subscriptionId;

    private final TeleTypeServiceImpl teleTypeService;

    private ExecutorProvider getExecutor() {
        return InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(THREAD_COUNT).build();
    }

    public void createSubscription() {

        log.info("Creating subscription.");
        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId);

        log.info("subscription name : {}", subscriptionName);

        MessageReceiver receiver =
                (PubsubMessage message, AckReplyConsumer consumer) -> {

                    log.info("Id: " + message.getMessageId());
                    log.info("Data: " + message.getData().toStringUtf8());

                    consumer.ack();

                    try {
                        teleTypeService.processMessage(message.getData().toStringUtf8());
                    } catch (JAXBException e) {
                        log.error("Exception occurred : {}", e.getMessage());
                        e.printStackTrace();
                    }
                };

        Subscriber subscriber = null;
        ExecutorProvider executorProvider = getExecutor();

        subscriber = getSubscriber(subscriptionName, receiver, executorProvider);

        subscriber.addListener(getSubscriberListener(executorProvider), MoreExecutors.directExecutor());

        subscriber.startAsync().awaitRunning();
        log.info("Listening for messages on {} : ", subscriptionName.toString());
        //subscriber.awaitTerminated(30, TimeUnit.SECONDS);

    }

    private Subscriber getSubscriber(ProjectSubscriptionName subscriptionName, MessageReceiver receiver, ExecutorProvider executorProvider) {
        return Subscriber.newBuilder(subscriptionName, receiver)
                .setExecutorProvider(executorProvider)
                .build();
    }

    private Listener getSubscriberListener(ExecutorProvider executorProvider) {
        return new Subscriber.Listener() {
            public void failed(Subscriber.State from, Throwable failure) {
                log.error(String.valueOf(failure.getStackTrace()));
                if (!executorProvider.getExecutor().isShutdown()) {
                    createSubscription();
                }
            }
        };
    }
}
