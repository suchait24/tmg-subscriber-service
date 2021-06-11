package com.infogain.gcp.poc.consumer.util;

import com.infogain.gcp.poc.consumer.dto.BatchRecord;
import com.infogain.gcp.poc.consumer.entity.BatchEventEntity;
import com.infogain.gcp.poc.consumer.entity.TeleTypeEntity;

import java.util.List;
import java.util.UUID;

public class BatchEventEntityUtil {

    public static BatchEventEntity createBatchEventEntity(List<TeleTypeEntity> teleTypeEntityList, BatchRecord batchRecord, String subscriberId) {

        return BatchEventEntity.builder()
                .batchEventLogId(UUID.randomUUID().toString())
                .batchMessageId(batchRecord.getBatchMessageId())
                .subscriberId(subscriberId)
                .totalMessageBatchCount(teleTypeEntityList.size())
                //.batchReceivedTime(batchRecord.getBatchReceivedTime())
                .build();
    }

}
