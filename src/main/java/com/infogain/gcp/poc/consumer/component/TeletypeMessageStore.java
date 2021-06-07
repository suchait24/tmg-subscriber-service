package com.infogain.gcp.poc.consumer.component;

import com.infogain.gcp.poc.consumer.entity.TeleTypeEntity;
import com.infogain.gcp.poc.consumer.repository.TASRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeletypeMessageStore {

    private final TASRepository tasRepository;

    public void saveMessagesList(List<TeleTypeEntity> teleTypeEntityList) {
        log.info("Saving all messages");
        tasRepository.saveAll(teleTypeEntityList);
        log.info("All messages saved in database.");

    }
}
