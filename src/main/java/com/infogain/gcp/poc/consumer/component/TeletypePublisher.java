package com.infogain.gcp.poc.consumer.component;


import com.google.pubsub.v1.PubsubMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Service
public class TeletypePublisher {

    private final BatchPublisher batchPublisher;
    private final BasicPublisher basicPublisher;

    public void processPublish(List<PubsubMessage> teletypeDataDTOMessagesList) throws InterruptedException, ExecutionException, JAXBException, IOException {
        //log.info("Publishing the message to topic.");

        //log.info("All messages List : {}", String.valueOf(teletypeDataDTOMessagesList));
        batchPublisher.publishMessage(teletypeDataDTOMessagesList);
        //log.info("Messages have been successfully published.");
    }

    public void processPublish1(List<CompletableFuture<PubsubMessage>> teletypeDataDTOMessagesList) throws InterruptedException, ExecutionException, JAXBException, IOException {
        //log.info("Publishing the message to topic.");

        //log.info("All messages List : {}", String.valueOf(teletypeDataDTOMessagesList));
        basicPublisher.publishMessage(teletypeDataDTOMessagesList);
        //log.info("Messages have been successfully published.");
    }

    public void processPublish2(PubsubMessage pubsubMessage) throws IOException, InterruptedException {
        basicPublisher.publishSingleMessage(pubsubMessage);
    }
}
