package com.example.capabilities;

import com.example.capabilities.domain.enums.Message;
import com.example.capabilities.domain.exceptions.DomainException;
import com.example.capabilities.domain.model.Capability;
import com.example.capabilities.domain.model.CapabilityWithTechnologies;
import com.example.capabilities.domain.spi.ICapabilityPersistencePort;
import com.example.capabilities.domain.usecase.CapabilityUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CapabilityUseCaseTest {

    private ICapabilityPersistencePort persistencePort;
    private CapabilityUseCase useCase;

    @BeforeEach
    void setUp() {
        persistencePort = Mockito.mock(ICapabilityPersistencePort.class);
        useCase = new CapabilityUseCase(persistencePort);
    }

    @Test
    void registerCapability_success() {
        Capability cap = new Capability(null, "DevOps", "Capacidad DevOps");
        List<UUID> techs = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        when(persistencePort.existsByName("DevOps")).thenReturn(Mono.just(false));
        when(persistencePort.validateTechnologiesExist(techs)).thenReturn(Mono.just(true));
        when(persistencePort.saveCapability(any(Capability.class)))
                .thenReturn(Mono.just(new Capability(UUID.randomUUID(), "DevOps", "Capacidad DevOps")));
        when(persistencePort.saveCapabilityTechnologies(any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.registerCapability(cap, techs))
                .expectNextMatches(c -> c.name().equals("DevOps"))
                .verifyComplete();
    }

    @Test
    void registerCapability_duplicateTechnologies() {
        Capability cap = new Capability(null, "Backend", "Capacidad Backend");

        List<UUID> techs = new ArrayList<>();
        UUID commonId = UUID.randomUUID();
        techs.add(commonId);
        techs.add(commonId);
        techs.add(UUID.randomUUID());

        StepVerifier.create(useCase.registerCapability(cap, techs))
                .expectErrorMatches(e -> e instanceof DomainException &&
                        ((DomainException) e).getMessage().equals(Message.DUPLICATE_TECHNOLOGIES.getMessage()))
                .verify();
    }

    @Test
    void registerCapability_invalidTechnologyCount() {
        Capability cap = new Capability(null, "Frontend", "Capacidad Frontend");
        List<UUID> techs = List.of(UUID.randomUUID()); // solo 1 tecnología

        StepVerifier.create(useCase.registerCapability(cap, techs))
                .expectErrorMatches(e -> e instanceof DomainException &&
                        ((DomainException) e).getMessage().equals(Message.RANGE_TECHNOLOGIES.getMessage()))
                .verify();
    }

    @Test
    void registerCapability_alreadyExists() {
        Capability cap = new Capability(null, "Data", "Capacidad Data");
        List<UUID> techs = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        when(persistencePort.existsByName("Data")).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.registerCapability(cap, techs))
                .expectErrorMatches(e -> e instanceof DomainException &&
                        ((DomainException) e).getMessage().equals(Message.CAPABILITY_ALREADY_EXISTS.getMessage()))
                .verify();
    }

    @Test
    void listCapabilities_success() {
        CapabilityWithTechnologies c1 = new CapabilityWithTechnologies(UUID.randomUUID(), "C1", List.of());
        CapabilityWithTechnologies c2 = new CapabilityWithTechnologies(UUID.randomUUID(), "C2", List.of());

        when(persistencePort.findAll(0, 10, "name", "asc")).thenReturn(Flux.just(c1, c2));

        StepVerifier.create(useCase.listCapabilities(0, 10, "name", "asc"))
                .expectNext(c1)
                .expectNext(c2)
                .verifyComplete();
    }

    @Test
    void countAll_success() {
        when(persistencePort.countAll()).thenReturn(Mono.just(5L));

        StepVerifier.create(useCase.countAll())
                .expectNext(5L)
                .verifyComplete();
    }

    @Test
    void listAllCapabilities_success() {
        Capability c1 = new Capability(UUID.randomUUID(), "C1", "Desc");
        Capability c2 = new Capability(UUID.randomUUID(), "C2", "Desc");

        when(persistencePort.findAll()).thenReturn(Flux.just(c1, c2));

        StepVerifier.create(useCase.listAllCapabilities())
                .expectNext(c1)
                .expectNext(c2)
                .verifyComplete();
    }
}
