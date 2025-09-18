package com.example.capabilities.infrastructure.adapter.repository;

import com.example.capabilities.infrastructure.adapter.entity.CapabilityTechnologyEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface CapabilityTechnologyRepository extends ReactiveCrudRepository<CapabilityTechnologyEntity, UUID> {

    @Query("INSERT INTO capability_technology (id, capability_id, technology_id) VALUES (:id, :capabilityId, :technologyId)")
    Mono<Void> saveCapabilityTechnology(UUID id, UUID capabilityId, UUID technologyId);

    @Query("SELECT * FROM capability_technology WHERE capability_id = :capabilityId")
    Flux<CapabilityTechnologyEntity> findByCapabilityId(UUID capabilityId);
}
