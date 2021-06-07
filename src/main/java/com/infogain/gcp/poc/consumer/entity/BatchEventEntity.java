package com.infogain.gcp.poc.consumer.entity;

import com.google.cloud.Timestamp;
import lombok.*;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Table(name = "BATCH_EVENT_LOG")
public class BatchEventEntity {

    @PrimaryKey
    @Column(name = "BATCH_EVENT_LOG_ID")
    @Id
    private String batchEventLogId;

    @Column(name = "SUBSCRIBER_ID")
    private String subscriberId;

    @Column(name = "BATCH_MESSAGE_ID")
    private Integer batchMessageId;

    @Column(name = "BATCH_RECEIVED_TIME")
    private Timestamp batchReceivedTime;

    @Column(name = "TOTAL_MESSAGES_BATCH_COUNT")
    private Integer totalMessageBatchCount;
}
