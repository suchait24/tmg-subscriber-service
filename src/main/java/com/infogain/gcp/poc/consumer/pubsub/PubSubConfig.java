package com.infogain.gcp.poc.consumer.pubsub;

import com.infogain.gcp.poc.consumer.service.PullSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.pubsub.support.converter.PubSubMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class PubSubConfig {

    private final PullSubscriptionService pullDemo;

    @Value("${spring.cloud.gcp.project-id}")
    private String projectId;
    @Value("${app.subscription.id}")
    private String subscriptionId;

    @Bean
    public PubSubMessageConverter pubSubMessageConverter() {
        return new XmlPubSubMessageConverter();
    }

    @Bean
    ThreadPoolTaskScheduler pubsubSubscriberThreadPool() {
        return new ThreadPoolTaskScheduler();
    }

    /*
    @PostConstruct
    void runPullAlways(PubSubSubscriberTemplate subscriberTemplate) {
        new Thread(() -> pullDemo.pullMessage(subscriberTemplate));
        }*/


        //List<PubsubMessage> list = subscriberTemplate.pullAndAck( ProjectSubscriptionName.of(projectId, subscriptionId).toString(), 100, true);
        //List<String> dtoList = list.stream().map(msg -> msg.getData().toStringUtf8()).collect(Collectors.toList());

        //dtoList.forEach(msg -> log.info("msg : {}", msg));

        //new Thread(() -> pullDemo.pullMessage(subscriberTemplate));
}
