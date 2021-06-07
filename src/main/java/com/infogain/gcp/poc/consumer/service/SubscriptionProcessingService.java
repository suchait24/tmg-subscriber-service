package com.infogain.gcp.poc.consumer.service;

import com.infogain.gcp.poc.consumer.dto.TeletypeEventDTO;
import com.infogain.gcp.poc.consumer.entity.TeleTypeEntity;
import com.infogain.gcp.poc.consumer.repository.TASRepository;
import com.infogain.gcp.poc.consumer.util.TeleTypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubscriptionProcessingService {

    private static Integer DEFAULT_SEQUENCE_NUMBER = 10;
    private final TASRepository tasRepository;

    public void processSubscriptionMessagesList(List<TeletypeEventDTO> teletypeEventDTOList) {

        log.info("Started processing subscription messages list , total records found : {}", teletypeEventDTOList.size());

       log.info("printing record here ---- ");

       teletypeEventDTOList.forEach(msg -> log.info("data record : {}", msg));

       List<TeleTypeEntity> teleTypeEntityList = teletypeEventDTOList.stream()
               .map(record -> {
                   try {
                       return TeleTypeUtil.convert(record, TeleTypeUtil.marshall(record), DEFAULT_SEQUENCE_NUMBER++);
                   } catch (JAXBException e) {
                       e.printStackTrace();
                   }
                   return null;
               })
               .collect(Collectors.toList());

       saveMessagesList(teleTypeEntityList);

       log.info("Processing stopped, all records processed  : {}", teletypeEventDTOList.size());

    }

    private void saveMessagesList(List<TeleTypeEntity> teleTypeEntityList) {
        log.info("Saving all messages");
        tasRepository.saveAll(teleTypeEntityList);
        log.info("All messages saved in database.");

    }
}
