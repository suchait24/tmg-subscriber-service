package com.infogain.gcp.poc.consumer.component;

import com.infogain.gcp.poc.consumer.entity.BatchEventEntity;
import com.infogain.gcp.poc.consumer.repository.BatchEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchStore {

    private final BatchEventRepository batchEventRepository;
    public void saveBatchEventEntity(BatchEventEntity batchEventEntity) {
        log.info("Saving batch event entity to database");
        batchEventRepository.save(batchEventEntity);
        log.info("batch event entity successfully saved.");
    }
}
