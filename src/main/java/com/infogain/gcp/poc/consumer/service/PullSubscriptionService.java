package com.infogain.gcp.poc.consumer.service;

import com.google.pubsub.v1.ProjectSubscriptionName;
import com.infogain.gcp.poc.consumer.dto.TeletypeEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.pubsub.core.subscriber.PubSubSubscriberTemplate;
import org.springframework.cloud.gcp.pubsub.support.converter.ConvertedAcknowledgeablePubsubMessage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PullSubscriptionService {

    @Value("${spring.cloud.gcp.project-id}")
    private String projectId;
    @Value("${app.subscription.id}")
    private String subscriptionId;

    private final SubscriptionProcessingService subscriptionProcessingService;

    public void pullMessage(PubSubSubscriberTemplate subscriberTemplate) {

        //log.info("Starting batch pulling.");

        List<ConvertedAcknowledgeablePubsubMessage<TeletypeEventDTO>> msgs = subscriberTemplate
                .pullAndConvert(ProjectSubscriptionName.of(projectId, subscriptionId).toString(), 100, true, TeletypeEventDTO.class);
        msgs.forEach(msg -> {
            log.info("message received : {}", msg.getPayload().toString());
            msg.ack();
        });

        if (!msgs.isEmpty()) {
            List<TeletypeEventDTO> teletypeEventDTOList = msgs.stream().map(msg -> msg.getPayload()).collect(Collectors.toList());
            subscriptionProcessingService.processSubscriptionMessagesList(teletypeEventDTOList);
        }


        //log.info("Stopping batch pull.");
    }
}
