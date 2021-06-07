package com.infogain.gcp.poc.consumer.repository;

import com.infogain.gcp.poc.consumer.entity.BatchEventEntity;
import org.springframework.cloud.gcp.data.spanner.repository.SpannerRepository;

public interface BatchEventRepository extends SpannerRepository<BatchEventEntity, String> {
}
