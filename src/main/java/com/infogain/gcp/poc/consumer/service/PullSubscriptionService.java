package com.infogain.gcp.poc.consumer.service;

import com.google.pubsub.v1.ProjectSubscriptionName;
import com.infogain.gcp.poc.consumer.dto.MessageDTO;

import com.google.pubsub.v1.ReceivedMessage;
import com.infogain.gcp.poc.consumer.component.PubSubSubscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PullSubscriptionService {

    @Value("${spring.cloud.gcp.project-id}")
    private String projectId;

    @Value("${app.subscription.id}")
    private String subscriptionId;

    @Value("${app.subscription.max.pull.count}")
    private Integer maxMessagePullCount;

    private final SubscriptionProcessingService subscriptionProcessingService;
    private final PubSubSubscriber pubSubSubscriber;

    public void pullMessages() throws InterruptedException, ExecutionException, JAXBException, IOException {

        List<ConvertedAcknowledgeablePubsubMessage<MessageDTO>> msgs = subscriberTemplate
                .pullAndConvert(ProjectSubscriptionName.of(projectId, subscriptionId).toString(), 100, true, MessageDTO.class);
        List<ReceivedMessage> receivedMessageList = pubSubSubscriber.getPullResponse();

        LocalDateTime batchReceivedTime = LocalDateTime.now();

        msgs.forEach(msg -> msg.getPayload().getMessageBody());

        //acknowledge only when batch is successfully processed.
        subscriptionProcessingService.processMessages(msgs, batchReceivedTime);
        if(!receivedMessageList.isEmpty()) {
            LocalDateTime batchReceivedTime = LocalDateTime.now();
            List<String> ackIds = subscriptionProcessingService.processMessages(receivedMessageList, batchReceivedTime);
            pubSubSubscriber.acknowledgeMessageList(ackIds);
        }
    }

}
