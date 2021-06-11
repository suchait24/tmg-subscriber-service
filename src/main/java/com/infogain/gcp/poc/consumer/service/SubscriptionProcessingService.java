package com.infogain.gcp.poc.consumer.service;

import com.infogain.gcp.poc.consumer.component.BatchStore;
import com.infogain.gcp.poc.consumer.component.TeletypePublisher;
import com.infogain.gcp.poc.consumer.dto.BatchRecord;
import com.infogain.gcp.poc.consumer.dto.TeletypeDataDTO;
import com.infogain.gcp.poc.consumer.dto.TeletypeEventDTO;
import com.infogain.gcp.poc.consumer.util.BatchRecordUtil;
import com.infogain.gcp.poc.consumer.util.TeletypeDataDTOUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gcp.pubsub.support.converter.ConvertedAcknowledgeablePubsubMessage;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubscriptionProcessingService {

    private static final String SUBSCRIBER_ID = "S1";
    private final BatchStore batchStore;

    private final TeletypePublisher teletypePublisher;

    public void processMessages(List<ConvertedAcknowledgeablePubsubMessage<TeletypeEventDTO>> msgs, LocalDateTime batchReceivedTime) throws InterruptedException, ExecutionException, IOException, JAXBException {

        if (!msgs.isEmpty()) {
            List<TeletypeEventDTO> teletypeEventDTOList = msgs.stream().map(msg -> msg.getPayload()).collect(Collectors.toList());
            BatchRecord batchRecord = BatchRecordUtil.createBatchRecord(teletypeEventDTOList, batchReceivedTime);
            processSubscriptionMessagesList(batchRecord);

            //send acknowledge for all processed messages
            msgs.forEach(msg -> msg.ack());
        }
    }

    private void processSubscriptionMessagesList(BatchRecord batchRecord) throws InterruptedException, ExecutionException, IOException, JAXBException {

        AtomicReference<Integer> sequencerNumber = new AtomicReference<>(1);

        Instant start = Instant.now();

        List<TeletypeEventDTO> teletypeEventDTOList = null;

        if (!batchRecord.getDtoList().isEmpty())
            teletypeEventDTOList = batchRecord.getDtoList();

        log.info("Started processing subscription messages list , total records found : {}", teletypeEventDTOList.size());

        List<String> teletypeEventDTOMessages = teletypeEventDTOList.stream()
                .map(record -> TeletypeDataDTOUtil.getTeletypeDataDTOMessage(record, sequencerNumber.getAndSet(sequencerNumber.get() + 1), batchRecord.getBatchMessageId()))
                .map(teletypeDataDTO -> wrapTeletypeConversionException(teletypeDataDTO))
                .collect(Collectors.toList());

        //send all processed messages to another topic.
        teletypePublisher.processPublish(teletypeEventDTOMessages);

        log.info("Processing stopped, all records processed  : {}", teletypeEventDTOList.size());

        Instant end = Instant.now();
        log.info("total time taken to process {} records is {} ms", teletypeEventDTOList.size(), Duration.between(start, end).toMillis());

    }

    private String wrapTeletypeConversionException(TeletypeDataDTO teletypeDataDTO) {

        try {
            return TeletypeDataDTOUtil.marshall(teletypeDataDTO);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

}
