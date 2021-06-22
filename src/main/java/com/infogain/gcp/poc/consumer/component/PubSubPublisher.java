package com.infogain.gcp.poc.consumer.component;

import com.google.cloud.pubsub.v1.Publisher;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Getter
@Setter
@Component
public class PubSubPublisher {

    private String topicName;
    private Publisher publisher;

    public PubSubPublisher(@Qualifier("teletypetopicName") String topicName) throws IOException {
        this.topicName = topicName;
        this.publisher = Publisher.newBuilder(topicName).build();
    }
}
