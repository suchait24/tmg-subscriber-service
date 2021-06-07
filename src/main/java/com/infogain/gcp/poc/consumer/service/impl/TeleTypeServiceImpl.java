package com.infogain.gcp.poc.consumer.service.impl;

import com.infogain.gcp.poc.consumer.dto.TeletypeEventDTO;
import com.infogain.gcp.poc.consumer.entity.TeleTypeEntity;
import com.infogain.gcp.poc.consumer.repository.TASRepository;
import com.infogain.gcp.poc.consumer.service.TeleTypeService;
import com.infogain.gcp.poc.consumer.util.TeleTypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeleTypeServiceImpl implements TeleTypeService {

    private static Integer DEFAULT_SEQUENCE_NUMBER = 10;

    private final TASRepository tasRepository;

    @Override
    public void processMessage(String message) throws JAXBException {

        TeletypeEventDTO teletypeEventDTO = TeleTypeUtil.unmarshall(message);
        log.info("object unmarshalled : {}", teletypeEventDTO);

        TeleTypeEntity teleTypeEntity = TeleTypeUtil.convert(teletypeEventDTO, message, DEFAULT_SEQUENCE_NUMBER++);
        log.info("converting dto to entity : {}", teleTypeEntity);

        saveMessage(teleTypeEntity);
    }

    private void saveMessage(TeleTypeEntity teleTypeEntity) {

        log.info("Inside save message method.");
        log.info(teleTypeEntity.toString());

        tasRepository.save(teleTypeEntity);
        //tasReactiveRepository.save(teleTypeEntity);
        log.info("message has been stored in db successfully.");
    }
}
