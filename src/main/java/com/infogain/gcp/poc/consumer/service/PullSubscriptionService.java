package com.infogain.gcp.poc.consumer.service;

import com.google.pubsub.v1.ProjectSubscriptionName;
import com.infogain.gcp.poc.consumer.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.gcp.pubsub.core.subscriber.PubSubSubscriberTemplate;
import org.springframework.cloud.gcp.pubsub.support.converter.ConvertedAcknowledgeablePubsubMessage;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class PullSubscriptionService {

    @Value("${spring.cloud.gcp.project-id}")
    private String projectId;
    @Value("${app.subscription.id}")
    private String subscriptionId;

    private final SubscriptionProcessingService subscriptionProcessingService;

    public void pullMessage(PubSubSubscriberTemplate subscriberTemplate) throws InterruptedException, ExecutionException, JAXBException, IOException {

        List<ConvertedAcknowledgeablePubsubMessage<MessageDTO>> msgs = subscriberTemplate
                .pullAndConvert(ProjectSubscriptionName.of(projectId, subscriptionId).toString(), 100, true, MessageDTO.class);

        LocalDateTime batchReceivedTime = LocalDateTime.now();

        msgs.forEach(msg -> msg.getPayload().getMessageBody());

        //acknowledge only when batch is successfully processed.
        subscriptionProcessingService.processMessages(msgs, batchReceivedTime);
    }

}
