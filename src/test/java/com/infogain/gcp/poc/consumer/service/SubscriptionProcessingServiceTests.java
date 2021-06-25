package com.infogain.gcp.poc.consumer.service;

import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.ReceivedMessage;
import com.infogain.gcp.poc.consumer.dto.AddressLine;
import com.infogain.gcp.poc.consumer.dto.MessageBody;
import com.infogain.gcp.poc.consumer.dto.TeletypeEventDTO;
import com.infogain.gcp.poc.consumer.helper.TeletypeEventDTOUtil;
import com.infogain.gcp.poc.consumer.util.TeleTypeUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

@ExtendWith(MockitoExtension.class)
public class SubscriptionProcessingServiceTests {

    @InjectMocks
    private SubscriptionProcessingService subscriptionProcessingService;

    @Mock
    private BatchService batchService;

    @Test
    public void testProcessMessages() throws JAXBException, InterruptedException, ExecutionException, IOException {


        TeletypeEventDTO teletypeEventDTO = TeletypeEventDTOUtil.getDefaultTeletypeEventDTO();

        PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                .setData(ByteString.copyFromUtf8(TeleTypeUtil.marshall(teletypeEventDTO)))
                .build();
        ReceivedMessage receivedMessage = ReceivedMessage.newBuilder().setMessage(pubsubMessage).build();
        List<ReceivedMessage> receivedMessageList = List.of(receivedMessage);

        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {

            }
        });
        futureList.add(future1);

        Mockito.when(batchService.processSubscriptionMessagesList(Mockito.anyObject(), Mockito.anyObject())).thenReturn(futureList);
        List<String> ackIds = subscriptionProcessingService.processMessages(receivedMessageList, LocalDateTime.now(), Instant.now());

        Assertions.assertEquals(ackIds.size(), 1);

    }

    @Test
    public void testRetrieveTeletypeEventDTOList() throws JAXBException {

        TeletypeEventDTO teletypeEventDTO = TeletypeEventDTOUtil.getDefaultTeletypeEventDTO();

        PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                .setData(ByteString.copyFromUtf8(TeleTypeUtil.marshall(teletypeEventDTO)))
                .build();
        ReceivedMessage receivedMessage = ReceivedMessage.newBuilder().setMessage(pubsubMessage).build();
        List<TeletypeEventDTO> teletypeEventDTOList = ReflectionTestUtils.invokeMethod(subscriptionProcessingService, "retrieveTeletypeEventDTOList", List.of(receivedMessage));
        Assertions.assertEquals(teletypeEventDTOList.size(), 1);
    }

    @Test
    public void testRetrieveTeletypeEventDTOListWithError() throws JAXBException {

        PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                .setData(ByteString.copyFromUtf8("any-message"))
                .build();
        ReceivedMessage receivedMessage = ReceivedMessage.newBuilder().setMessage(pubsubMessage).build();
        List<TeletypeEventDTO> teletypeEventDTOList = ReflectionTestUtils.invokeMethod(subscriptionProcessingService, "retrieveTeletypeEventDTOList", List.of(receivedMessage));
        Assertions.assertEquals(teletypeEventDTOList.get(0), null);
    }


}
