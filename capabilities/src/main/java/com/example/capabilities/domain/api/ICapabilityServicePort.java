package com.example.capabilities.domain.api;

import com.example.capabilities.domain.model.Capability;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface ICapabilityServicePort {
    Mono<Capability> registerCapability(Capability capability, List<UUID> technologies);

}