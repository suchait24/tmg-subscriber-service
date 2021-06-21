package com.infogain.gcp.poc.consumer.util;

import com.infogain.gcp.poc.consumer.dto.BatchRecord;
import com.infogain.gcp.poc.consumer.dto.MessageDTO;

import java.time.LocalDateTime;
import java.util.List;

public class BatchRecordUtil {

    private static Integer BATCH_MESSAGE_ID = 1;

    public static BatchRecord createBatchRecord(List<MessageDTO> messageDTOList, LocalDateTime batchReceivedTime) {

        BatchRecord batchRecord = new BatchRecord();
        batchRecord.setDtoList(messageDTOList);
        batchRecord.setBatchMessageId(BATCH_MESSAGE_ID++);
        batchRecord.setBatchReceivedTime(batchReceivedTime);

        return batchRecord;
    }

}
