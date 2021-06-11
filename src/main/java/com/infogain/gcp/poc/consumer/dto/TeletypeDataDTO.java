package com.infogain.gcp.poc.consumer.dto;

import com.google.cloud.Timestamp;
import lombok.*;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@XmlRootElement(name = "Teletype")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"hostLocator", "messageCorrelationId","carrierCode","createdTimestamp","updatedTimestamp","sequenceNumber","batchId"})
public class TeletypeDataDTO {

    @XmlElement(name = "host_locator")
    private String hostLocator;

    @XmlElement(name = "message_correlation_id")
    private String messageCorrelationId;

    @XmlElement(name = "carrier_code")
    private String carrierCode;

    @XmlElement(name = "created")
    private String createdTimestamp;

    @XmlElement(name = "updated")
    private String updatedTimestamp;

    @XmlElement(name = "sequencer_number")
    private Integer sequenceNumber;

    @XmlElement(name = "batch_id")
    private Integer batchId;
}
