package com.infogain.gcp.poc.consumer.pubsub;

import com.google.pubsub.v1.PubsubMessage;
import com.infogain.gcp.poc.consumer.util.TeleTypeUtil;
import lombok.SneakyThrows;
import org.springframework.cloud.gcp.pubsub.support.converter.PubSubMessageConverter;

import java.util.Map;

public class XmlPubSubMessageConverter implements PubSubMessageConverter {

    @Override
    public PubsubMessage toPubSubMessage(Object payload, Map<String, String> headers) {
        return null;
    }

    @SneakyThrows
    @Override
    public <T> T fromPubSubMessage(PubsubMessage message, Class<T> payloadType) {
        return (T) TeleTypeUtil.unmarshall(message.getData().toStringUtf8());
    }
}
