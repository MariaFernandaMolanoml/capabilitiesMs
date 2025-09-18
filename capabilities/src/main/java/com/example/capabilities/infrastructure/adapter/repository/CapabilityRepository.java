package com.example.capabilities.infrastructure.adapter.repository;

import com.example.capabilities.infrastructure.adapter.entity.CapabilityEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CapabilityRepository extends ReactiveCrudRepository<CapabilityEntity, UUID> {
    Mono<CapabilityEntity> findByName(String name);
}