package com.example.capabilities.domain.api;

import com.example.capabilities.domain.model.Capability;
import com.example.capabilities.domain.model.CapabilityWithTechnologies;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface ICapabilityServicePort {
    Mono<Capability> registerCapability(Capability capability, List<UUID> technologies);

    Flux<CapabilityWithTechnologies> listCapabilities(int page, int size, String sortBy, String order);
    Mono<Long> countAll();
}