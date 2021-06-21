package com.infogain.gcp.poc.consumer.util;

import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.infogain.gcp.poc.consumer.dto.TeletypeEventDTO;

import javax.xml.bind.JAXBException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PubSubMessageHelper {

    private static final String BATCH_ID = "batch_id";
    private static final String SEQUENCE_NUMBER = "sequence_number";
    private static final String CREATED_TIME = "created_time";

    public static PubsubMessage getPubSubMessage(TeletypeEventDTO teletypeEventDTO, Integer sequenceNumber, Integer batchId) throws JAXBException {

        Map<String, String> attributesMap = getAttributesMap(String.valueOf(sequenceNumber), String.valueOf(batchId));

        return PubsubMessage.newBuilder()
                .setData(ByteString.copyFromUtf8(TeleTypeUtil.marshall(teletypeEventDTO)))
                .putAllAttributes(attributesMap)
                .build();

    }

    private static Map<String, String> getAttributesMap(String sequenceNumber, String batchId) {

        Map<String, String> attributesMap = new HashMap<>();
        attributesMap.put(BATCH_ID, String.valueOf(batchId));
        attributesMap.put(SEQUENCE_NUMBER, String.valueOf(sequenceNumber));
        attributesMap.put(CREATED_TIME,String.valueOf(LocalDateTime.now()));

        return attributesMap;
    }
}
