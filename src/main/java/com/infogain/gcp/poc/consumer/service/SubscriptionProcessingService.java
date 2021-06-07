package com.infogain.gcp.poc.consumer.service;

import com.infogain.gcp.poc.consumer.dto.BatchRecord;
import com.infogain.gcp.poc.consumer.dto.TeletypeEventDTO;
import com.infogain.gcp.poc.consumer.entity.BatchEventEntity;
import com.infogain.gcp.poc.consumer.entity.TeleTypeEntity;
import com.infogain.gcp.poc.consumer.repository.BatchEventRepository;
import com.infogain.gcp.poc.consumer.repository.TASRepository;
import com.infogain.gcp.poc.consumer.util.TeleTypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubscriptionProcessingService {

    private static Integer DEFAULT_SEQUENCE_NUMBER = 1;
    private final TASRepository tasRepository;
    private final BatchEventRepository batchEventRepository;
    private static final String SUBSCRIBER_ID = "S1";

    public void processSubscriptionMessagesList(BatchRecord batchRecord) {

        List<TeletypeEventDTO> teletypeEventDTOList = null;

        if(!batchRecord.getDtoList().isEmpty())
            teletypeEventDTOList = batchRecord.getDtoList();

        log.info("Started processing subscription messages list , total records found : {}", teletypeEventDTOList.size());

       List<TeleTypeEntity> teleTypeEntityList = teletypeEventDTOList.stream()
               .map(record -> {
                   try {
                       return TeleTypeUtil.convert(record, TeleTypeUtil.marshall(record), DEFAULT_SEQUENCE_NUMBER++);
                   } catch (JAXBException e) {
                       e.printStackTrace();
                   }
                   return null;
               })
               .collect(Collectors.toList());

       saveMessagesList(teleTypeEntityList);

       log.info("Processing stopped, all records processed  : {}", teletypeEventDTOList.size());
       log.info("Logging batch to database now.");

       BatchEventEntity batchEventEntity = createBatchEventEntity(teleTypeEntityList, batchRecord);
       log.info("Batch entity generated : {}", batchEventEntity);

        saveBatchEventEntity(batchEventEntity);

    }

    private void saveMessagesList(List<TeleTypeEntity> teleTypeEntityList) {
        log.info("Saving all messages");
        tasRepository.saveAll(teleTypeEntityList);
        log.info("All messages saved in database.");

    }

    private BatchEventEntity createBatchEventEntity(List<TeleTypeEntity> teleTypeEntityList, BatchRecord batchRecord) {

        return BatchEventEntity.builder()
                .batchEventLogId(UUID.randomUUID().toString())
                .batchMessageId(batchRecord.getBatchMessageId())
                .subscriberId(SUBSCRIBER_ID)
                .totalMessageBatchCount(teleTypeEntityList.size())
                .batchReceivedTime(batchRecord.getBatchReceivedTime())
                .build();
    }

    private void saveBatchEventEntity(BatchEventEntity batchEventEntity) {
        log.info("Saving batch event entity to database");
        batchEventRepository.save(batchEventEntity);
        log.info("batch event entity successfully saved.");
    }
}
