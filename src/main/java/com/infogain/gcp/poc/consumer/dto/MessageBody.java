package com.infogain.gcp.poc.consumer.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.xml.bind.annotation.*;

@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"Line"})
public class MessageBody {
    @XmlElement(name="Line")
    private String[] Line;

}

