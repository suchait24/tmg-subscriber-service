package com.infogain.gcp.poc.consumer.service;


import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.infogain.gcp.poc.consumer.component.TeletypePublisher;
import com.infogain.gcp.poc.consumer.dto.AddressLine;
import com.infogain.gcp.poc.consumer.dto.BatchRecord;
import com.infogain.gcp.poc.consumer.dto.MessageBody;
import com.infogain.gcp.poc.consumer.dto.TeletypeEventDTO;
import com.infogain.gcp.poc.consumer.helper.TeletypeEventDTOUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

@ExtendWith(MockitoExtension.class)
public class BatchServiceTests {

    @InjectMocks
    private BatchService batchService;

    @Mock
    private TeletypePublisher teletypePublisher;

    @Test
    public void basicTest() throws InterruptedException, ExecutionException, JAXBException, IOException {

        BatchRecord batchRecord = new BatchRecord();
        batchRecord.setBatchMessageId(12);
        batchRecord.setBatchReceivedTime(LocalDateTime.now());


       TeletypeEventDTO teletypeEventDTO = TeletypeEventDTOUtil.getDefaultTeletypeEventDTO();

        List<TeletypeEventDTO> teletypeEventDTOList = List.of(teletypeEventDTO);
        batchRecord.setDtoList(teletypeEventDTOList);

        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {

            }
        });
        futureList.add(future1);

        List<CompletableFuture<Void>> completableFutures =  batchService.processSubscriptionMessagesList(batchRecord, Instant.now());
        Assertions.assertEquals(completableFutures.size(), 1);
    }

    @Test
    public void testPublishInParallel() {

        PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                .setData(ByteString.copyFromUtf8("any-message"))
                .build();

        List<PubsubMessage> pubsubMessages = new ArrayList<>();
        pubsubMessages.add(pubsubMessage);

        List<CompletableFuture<Void>> futureList =  ReflectionTestUtils.invokeMethod(batchService, "publishInParallel", pubsubMessages);
        Assertions.assertEquals(futureList.size(), 1);
    }

    @Test
    public void testPreparePubSubMessageList() {

        TeletypeEventDTO teletypeEventDTO = TeletypeEventDTOUtil.getDefaultTeletypeEventDTO();

        List<TeletypeEventDTO> teletypeEventDTOList = List.of(teletypeEventDTO);
        AtomicReference<Integer> sequencerNumber = new AtomicReference<>(1);

        BatchRecord batchRecord = new BatchRecord();
        batchRecord.setBatchMessageId(12);

        List<PubsubMessage> pubsubMessageList = ReflectionTestUtils.invokeMethod(batchService, "preparePubSubMessageList", teletypeEventDTOList, sequencerNumber, batchRecord);
        Assertions.assertEquals(pubsubMessageList.size(), 1);
    }

    @Test
    public void testWrapTeletypeConversionException() {

        TeletypeEventDTO teletypeEventDTO = TeletypeEventDTOUtil.getDefaultTeletypeEventDTO();

        PubsubMessage pubsubMessage = ReflectionTestUtils.invokeMethod(batchService, "wrapTeletypeConversionException", teletypeEventDTO, 1, 12);
        Assertions.assertNotNull(pubsubMessage);

    }

}
