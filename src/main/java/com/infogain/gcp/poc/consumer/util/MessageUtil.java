package com.infogain.gcp.poc.consumer.util;

import com.infogain.gcp.poc.consumer.dto.MessageDTO;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

@Slf4j
public class MessageUtil {

    public static MessageDTO unmarshall(String message) throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(MessageDTO.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        MessageDTO messageDTO = (MessageDTO) unmarshaller.unmarshal(new StringReader(message));

        //log.info("message dto generated : {}", messageDTO);

        return messageDTO;
    }

    public static String marshall(MessageDTO messageDTO) throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(MessageDTO.class);
        Marshaller marshaller = jaxbContext.createMarshaller();

        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(messageDTO, stringWriter);

        String result = stringWriter.toString();
        //log.info("Message XML generated : {}", result);

        return result;
    }
}
