package com.infogain.gcp.poc.consumer.dto;

import com.google.cloud.Timestamp;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BatchRecord {

    private List<TeletypeEventDTO> dtoList;
    private Integer batchMessageId;
    private Timestamp batchReceivedTime;
}
