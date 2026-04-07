package com.example.capabilities.infrastructure.adapter.repository;

import com.example.capabilities.infrastructure.adapter.entity.CapabilityEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CapabilityRepository extends ReactiveCrudRepository<CapabilityEntity, UUID> {
    Mono<CapabilityEntity> findByName(String name);

    @Query("SELECT * FROM capability ORDER BY name ASC LIMIT :limit OFFSET :offset")
    Flux<CapabilityEntity> findAllByPageAsc(int limit, int offset);

    @Query("SELECT * FROM capability ORDER BY name DESC LIMIT :limit OFFSET :offset")
    Flux<CapabilityEntity> findAllByPageDesc(int limit, int offset);
}
