package com.infogain.gcp.poc.consumer.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.xml.bind.annotation.*;

@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"Priority", "Destination"})
public class AddressLine {
    @XmlElement(name="Priority")
    private String Priority;
    @XmlElement(name="Destination")
    private String Destination;
}



