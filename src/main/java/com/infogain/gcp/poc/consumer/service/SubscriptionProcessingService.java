package com.infogain.gcp.poc.consumer.service;

import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.ReceivedMessage;
import com.infogain.gcp.poc.consumer.component.TeletypePublisher;
import com.infogain.gcp.poc.consumer.dto.BatchRecord;
import com.infogain.gcp.poc.consumer.dto.TeletypeEventDTO;
import com.infogain.gcp.poc.consumer.util.BatchRecordUtil;
import com.infogain.gcp.poc.consumer.util.TeleTypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubscriptionProcessingService {

    private static final String BATCH_ID = "batch_id";
    private static final String SEQUENCE_NUMBER = "sequence_number";
    private static final String CREATED_TIME = "created_time";

    private final TeletypePublisher teletypePublisher;
    private final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public List<String> processMessages(List<ReceivedMessage> msgs, LocalDateTime batchReceivedTime) throws InterruptedException, ExecutionException, IOException, JAXBException {

            log.info("Number of processors available : {}", Runtime.getRuntime().availableProcessors());

            //TODO - fix this once tested
            List<TeletypeEventDTO> teletypeEventDTOList = msgs.stream().map(msg -> {
                try {
                    return TeleTypeUtil.unmarshall(msg.getMessage().getData().toStringUtf8());
                } catch (JAXBException e) {
                    log.error("error occurred : {}", e.getMessage());
                }
                return null;
            }).collect(Collectors.toList());


            BatchRecord batchRecord = BatchRecordUtil.createBatchRecord(teletypeEventDTOList, batchReceivedTime);
            List<CompletableFuture<Void>> futureList  = processSubscriptionMessagesList(batchRecord);

            //send acknowledge for all processed messages
            futureList.stream()
                    .map(CompletableFuture::join);

            return msgs.stream()
                    .map(msg -> msg.getAckId())
                    .collect(Collectors.toList());
    }

    private List<CompletableFuture<Void>> processSubscriptionMessagesList(BatchRecord batchRecord) throws InterruptedException, ExecutionException, IOException, JAXBException {

        AtomicReference<Integer> sequencerNumber = new AtomicReference<>(1);

        Instant start = Instant.now();

        List<TeletypeEventDTO> teletypeEventDTOList = null;

        if (!batchRecord.getDtoList().isEmpty())
            teletypeEventDTOList = batchRecord.getDtoList();

        //log.info("Started processing subscription messages list , total records found : {}", teletypeEventDTOList.size());

        List<PubsubMessage> teletypeEventDTOMessages = teletypeEventDTOList.stream()
                .map(record -> wrapTeletypeConversionException(record, sequencerNumber.getAndSet(sequencerNumber.get() + 1), batchRecord.getBatchMessageId()))
                .collect(Collectors.toList());

        //send all processed messages to another topic.

        List<CompletableFuture<Void>> futureList = teletypeEventDTOMessages.stream()
                .map(message -> CompletableFuture.runAsync(() -> {
                    try {
                         teletypePublisher.processPublish(message);
                    } catch (IOException e) {
                        log.error("Error occurred : {}", e.getMessage());
                    } catch (InterruptedException e) {
                        log.error("Error occurred : {}", e.getMessage());
                    }
                }, THREAD_POOL)).collect(Collectors.toList());


        Instant end = Instant.now();
        Long totalTime = Duration.between(start, end).toMillis();
        log.info("total time taken to process {} records is {} ms", teletypeEventDTOList.size(), totalTime);

        return futureList;
    }

    private PubsubMessage getPubSubMessage(TeletypeEventDTO teletypeEventDTO, Integer sequenceNumber, Integer batchId) throws JAXBException {

        //log.info("Preparing pubsub message with attributes.");
        Map<String, String> attributesMap = getAttributesMap(String.valueOf(sequenceNumber), String.valueOf(batchId));

        return PubsubMessage.newBuilder()
                .setData(ByteString.copyFromUtf8(TeleTypeUtil.marshall(teletypeEventDTO)))
                .putAllAttributes(attributesMap)
                .build();

    }

    private Map<String, String> getAttributesMap(String sequenceNumber, String batchId) {

        Map<String, String> attributesMap = new HashMap<>();
        attributesMap.put(BATCH_ID, String.valueOf(batchId));
        attributesMap.put(SEQUENCE_NUMBER, String.valueOf(sequenceNumber));
        attributesMap.put(CREATED_TIME,String.valueOf(LocalDateTime.now()));

        return attributesMap;
    }

    private PubsubMessage wrapTeletypeConversionException(TeletypeEventDTO teletypeEventDTO, Integer sequenceNumber, Integer batchId) {
        try {
            return getPubSubMessage(teletypeEventDTO, sequenceNumber, batchId);
        } catch (JAXBException e) {
            log.error("Exception during marshalling : {}", e.getMessage());
        }
        return null;
    }

}
