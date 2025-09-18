package com.example.capabilities.domain.spi;


import com.example.capabilities.domain.model.Capability;
import com.example.capabilities.domain.model.CapabilityWithTechnologies;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface ICapabilityPersistencePort {
    Mono<Capability> saveCapability(Capability capability);
    Mono<Void> saveCapabilityTechnologies(UUID capabilityId, List<UUID> technologies);
    Mono<Boolean> existsByName(String name);
    Mono<Boolean> validateTechnologiesExist(List<UUID> technologies);
    Flux<CapabilityWithTechnologies> findAll(int page, int size, String sortBy, String order);
    Mono<Long> countAll();
}