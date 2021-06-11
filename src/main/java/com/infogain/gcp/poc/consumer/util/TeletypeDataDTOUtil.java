package com.infogain.gcp.poc.consumer.util;

import com.infogain.gcp.poc.consumer.dto.TeletypeDataDTO;
import com.infogain.gcp.poc.consumer.dto.TeletypeEventDTO;
import com.infogain.gcp.poc.consumer.entity.TeleTypeEntity;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.time.LocalDateTime;

@Slf4j
public class TeletypeDataDTOUtil {

    public static String marshall(TeletypeDataDTO teletypeEventDTO) throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(TeletypeDataDTO.class);
        Marshaller marshaller = jaxbContext.createMarshaller();

        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(teletypeEventDTO, stringWriter);

        String result = stringWriter.toString();
        log.info("Teletype XML generated : {}", result);

        return result;
    }

    public static String getTeletypeDataDTOMessage(TeletypeEventDTO teletypeEventDTO, Integer sequenceNumber, Integer batchId) {

        TeletypeDataDTO teletypeDataDTO = new TeletypeDataDTO();
        teletypeDataDTO.setBatchId(batchId);
        teletypeDataDTO.setCarrierCode(teletypeEventDTO.getCarrierCode());
        teletypeDataDTO.setHostLocator(teletypeEventDTO.getHostRecordLocator());
        teletypeDataDTO.setMessageCorrelationId(String.valueOf(teletypeEventDTO.getMessageCorelationId()));
        teletypeDataDTO.setSequenceNumber(sequenceNumber);
        teletypeDataDTO.setCreatedTimestamp(String.valueOf(LocalDateTime.now()));

       return teletypeDataDTO.toString();

    }
}
