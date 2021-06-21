package com.infogain.gcp.poc.consumer.pubsub;

import com.infogain.gcp.poc.consumer.service.PullSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartProcessing {

    private final PullSubscriptionService pullDemo;
    private final TaskExecutor taskExecutor;

    @PostConstruct
    void runPullAlways() {
        log.info("Starting pull mechanism in background.");

        //We can use a thread pool as well rather than using single thread.
        Thread subscriberThread = new Thread(() -> {
            while(true) {
                try {
                    pullDemo.pullMessages();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JAXBException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        subscriberThread.setName("subscriber-background-thread");
        subscriberThread.start();
    }
}
