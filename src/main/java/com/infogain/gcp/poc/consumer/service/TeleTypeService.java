package com.infogain.gcp.poc.consumer.service;

import javax.xml.bind.JAXBException;

public interface TeleTypeService {

    public void processMessage(String message) throws JAXBException;

}
