package com.example.capabilities.domain.usecase;

import com.example.capabilities.domain.api.ICapabilityServicePort;
import com.example.capabilities.domain.enums.Message;
import com.example.capabilities.domain.exceptions.DomainException;
import com.example.capabilities.domain.model.Capability;
import com.example.capabilities.domain.model.CapabilityWithTechnologies;
import com.example.capabilities.domain.spi.ICapabilityPersistencePort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CapabilityUseCase implements ICapabilityServicePort {

    private final ICapabilityPersistencePort capabilityPersistencePort;

    public CapabilityUseCase(ICapabilityPersistencePort capabilityPersistencePort) {
        this.capabilityPersistencePort = capabilityPersistencePort;
    }

    @Override
    public Mono<Capability> registerCapability(Capability capability, List<UUID> technologies) {
        if (technologies.size() < 3 || technologies.size() > 20) {
            return Mono.error(new DomainException(Message.RANGE_TECHNOLOGIES));
        }

        Set<UUID> uniqueTechs = new HashSet<>(technologies);
        if (uniqueTechs.size() != technologies.size()) {
            return Mono.error(new DomainException(Message.DUPLICATE_TECHNOLOGIES));
        }

        Capability capabilityToSave = new Capability(
                capability.id(),
                capability.name(),
                capability.description()
        );

        return capabilityPersistencePort.existsByName(capabilityToSave.name())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new DomainException(Message.CAPABILITY_ALREADY_EXISTS));
                    }
                    return capabilityPersistencePort.validateTechnologiesExist(technologies);
                })
                .flatMap(allExist -> {
                    if (!allExist) {
                        return Mono.error(new DomainException(Message.INVALID_TECHNOLOGIES));
                    }
                    return capabilityPersistencePort.saveCapability(capabilityToSave)
                            .flatMap(saved ->
                                    capabilityPersistencePort.saveCapabilityTechnologies(saved.id(), technologies)
                                            .thenReturn(saved)
                            );
                });
    }
    @Override
    public Flux<CapabilityWithTechnologies> listCapabilities(int page, int size, String sortBy, String order) {
        return capabilityPersistencePort.findAll(page, size, sortBy, order);
    }

    @Override
    public Mono<Long> countAll() {
        return capabilityPersistencePort.countAll();
    }

    @Override
    public Flux<Capability> listAllCapabilities() {
        return capabilityPersistencePort.findAll();
    }

}
