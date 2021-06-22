package com.infogain.gcp.poc.consumer.service;

import com.google.pubsub.v1.ReceivedMessage;
import com.infogain.gcp.poc.consumer.dto.BatchRecord;
import com.infogain.gcp.poc.consumer.dto.TeletypeEventDTO;
import com.infogain.gcp.poc.consumer.util.BatchRecordUtil;
import com.infogain.gcp.poc.consumer.util.TeleTypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubscriptionProcessingService {

    private final BatchService batchService;

    public List<String> processMessages(List<ReceivedMessage> receivedMessageList, LocalDateTime batchReceivedTime) throws InterruptedException, ExecutionException, IOException, JAXBException {

            log.info("Number of processors available : {}", Runtime.getRuntime().availableProcessors());

            List<TeletypeEventDTO> teletypeEventDTOList = retrieveTeletypeEventDTOList(receivedMessageList);

            BatchRecord batchRecord = BatchRecordUtil.createBatchRecord(teletypeEventDTOList, batchReceivedTime);
            List<CompletableFuture<Void>> futureList  = batchService.processSubscriptionMessagesList(batchRecord);

            //send acknowledge for all processed messages
            //futureList.stream()
              //      .map(CompletableFuture::join);

        CompletableFuture[] cfs = futureList.toArray(new CompletableFuture[futureList.size()]);
        CompletableFuture.allOf(cfs)
                .thenApply(ignored -> futureList.stream()
                .map(CompletableFuture::join));

        return receivedMessageList.stream()
                    .map(msg -> msg.getAckId())
                    .collect(Collectors.toList());
    }

    private List<TeletypeEventDTO> retrieveTeletypeEventDTOList(List<ReceivedMessage> receivedMessageList) {

        //TODO - fix this once tested
        return receivedMessageList.stream().map(msg -> {
            try {
                return TeleTypeUtil.unmarshall(msg.getMessage().getData().toStringUtf8());
            } catch (JAXBException e) {
                log.error("error occurred : {}", e.getMessage());
            }
            return null;
        }).collect(Collectors.toList());
    }

}
