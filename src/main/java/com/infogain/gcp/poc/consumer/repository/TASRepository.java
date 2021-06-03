package com.infogain.gcp.poc.consumer.repository;

import com.infogain.gcp.poc.consumer.entity.TeleTypeEntity;
import org.springframework.cloud.gcp.data.spanner.repository.SpannerRepository;

public interface TASRepository extends SpannerRepository<TeleTypeEntity, String> {
}
