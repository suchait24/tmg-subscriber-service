package com.infogain.gcp.poc.consumer.service;

import com.google.pubsub.v1.PubsubMessage;
import com.infogain.gcp.poc.consumer.component.TeletypePublisher;
import com.infogain.gcp.poc.consumer.dto.BatchRecord;
import com.infogain.gcp.poc.consumer.dto.TeletypeEventDTO;
import com.infogain.gcp.poc.consumer.util.PubSubMessageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BatchService {

    private final TeletypePublisher teletypePublisher;
    private final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    protected List<CompletableFuture<Void>> processSubscriptionMessagesList(BatchRecord batchRecord) throws InterruptedException, ExecutionException, IOException, JAXBException {

        AtomicReference<Integer> sequencerNumber = new AtomicReference<>(1);

        Instant start = Instant.now();

        List<TeletypeEventDTO> teletypeEventDTOList = null;

        if (!batchRecord.getDtoList().isEmpty())
            teletypeEventDTOList = batchRecord.getDtoList();

        List<PubsubMessage> teletypeEventDTOMessages = preparePubSubMessageList(teletypeEventDTOList, sequencerNumber, batchRecord);

        List<CompletableFuture<Void>> futureList = publishInParallel(teletypeEventDTOMessages);


        Instant end = Instant.now();
        Long totalTime = Duration.between(start, end).toMillis();
        log.info("total time taken to process {} records is {} ms", teletypeEventDTOList.size(), totalTime);
        return futureList;
    }

    private PubsubMessage wrapTeletypeConversionException(TeletypeEventDTO teletypeEventDTO, Integer sequenceNumber, Integer batchId) {
        try {
            return PubSubMessageHelper.getPubSubMessage(teletypeEventDTO, sequenceNumber, batchId);
        } catch (JAXBException e) {
            log.error("Exception during marshalling : {}", e.getMessage());
        }
        return null;
    }

    private List<CompletableFuture<Void>> publishInParallel(List<PubsubMessage> teletypeEventDTOMessages) {

        return teletypeEventDTOMessages.stream()
                .map(message -> CompletableFuture.runAsync(() -> {
                    try {
                        teletypePublisher.processPublish(message);
                    } catch (IOException e) {
                        log.error("Error occurred : {}", e.getMessage());
                    } catch (InterruptedException e) {
                        log.error("Error occurred : {}", e.getMessage());
                    }
                }, THREAD_POOL)).collect(Collectors.toList());
    }

    private List<PubsubMessage> preparePubSubMessageList(List<TeletypeEventDTO> teletypeEventDTOList, AtomicReference<Integer> sequenceNumber, BatchRecord batchRecord) {

        return teletypeEventDTOList.stream()
                .map(record -> wrapTeletypeConversionException(record, sequenceNumber.getAndSet(sequenceNumber.get() + 1), batchRecord.getBatchMessageId()))
                .collect(Collectors.toList());
    }
}
