package com.infogain.gcp.poc.consumer.service;


import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.infogain.gcp.poc.consumer.component.TeletypePublisher;
import com.infogain.gcp.poc.consumer.dto.BatchRecord;
import com.infogain.gcp.poc.consumer.dto.TeletypeEventDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.xml.bind.JAXBException;
import java.io.IOException;
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

        TeletypeEventDTO dto1 = new TeletypeEventDTO();
        dto1.setCarrierCode("123");
        dto1.setHostRecordLocator("AAA");
        dto1.setMessageCorelationId(1234L);

        TeletypeEventDTO dto2 = new TeletypeEventDTO();
        dto2.setCarrierCode("123");
        dto2.setHostRecordLocator("AAA");
        dto2.setMessageCorelationId(1234L);

        List<TeletypeEventDTO> teletypeEventDTOList = List.of(dto1, dto2);
        batchRecord.setDtoList(teletypeEventDTOList);

        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {

            }
        });
        futureList.add(future1);

        List<CompletableFuture<Void>> completableFutures =  batchService.processSubscriptionMessagesList(batchRecord);
        Assertions.assertEquals(completableFutures.size(), 2);
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

        TeletypeEventDTO dto1 = new TeletypeEventDTO();
        dto1.setCarrierCode("123");
        dto1.setHostRecordLocator("AAA");
        dto1.setMessageCorelationId(1234L);

        TeletypeEventDTO dto2 = new TeletypeEventDTO();
        dto2.setCarrierCode("123");
        dto2.setHostRecordLocator("AAA");
        dto2.setMessageCorelationId(1234L);

        List<TeletypeEventDTO> teletypeEventDTOList = List.of(dto1, dto2);
        AtomicReference<Integer> sequencerNumber = new AtomicReference<>(1);

        BatchRecord batchRecord = new BatchRecord();
        batchRecord.setBatchMessageId(12);

        List<PubsubMessage> pubsubMessageList = ReflectionTestUtils.invokeMethod(batchService, "preparePubSubMessageList", teletypeEventDTOList, sequencerNumber, batchRecord);
        Assertions.assertEquals(pubsubMessageList.size(), 2);
    }

    @Test
    public void testWrapTeletypeConversionException() {

        TeletypeEventDTO dto1 = new TeletypeEventDTO();
        dto1.setCarrierCode("123");
        dto1.setHostRecordLocator("AAA");
        dto1.setMessageCorelationId(1234L);

        PubsubMessage pubsubMessage = ReflectionTestUtils.invokeMethod(batchService, "wrapTeletypeConversionException", dto1, 1, 12);
        Assertions.assertNotNull(pubsubMessage);

    }

}
