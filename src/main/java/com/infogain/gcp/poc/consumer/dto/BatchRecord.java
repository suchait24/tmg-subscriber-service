package com.infogain.gcp.poc.consumer.dto;

import com.google.cloud.Timestamp;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BatchRecord {

    private List<MessageDTO> dtoList;
    private Integer batchMessageId;
    private LocalDateTime batchReceivedTime;
}
