package com.example.capabilities.application;

import com.example.capabilities.domain.api.ICapabilityServicePort;
import com.example.capabilities.domain.spi.ICapabilityPersistencePort;
import com.example.capabilities.domain.usecase.CapabilityUseCase;
import com.example.capabilities.infrastructure.adapter.CapabilityPersistenceAdapter;
import com.example.capabilities.infrastructure.adapter.mapper.CapabilityEntityMapper;
import com.example.capabilities.infrastructure.adapter.repository.CapabilityRepository;
import com.example.capabilities.infrastructure.adapter.repository.CapabilityTechnologyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class UseCasesConfig {

    private final CapabilityRepository capabilityRepository;
    private final CapabilityTechnologyRepository capabilityTechnologyRepository;
    private final CapabilityEntityMapper capabilityEntityMapper;
    private final WebClient webClient;

    @Bean
    public ICapabilityPersistencePort capabilityPersistencePort() {
        return new CapabilityPersistenceAdapter(
                capabilityRepository,
                capabilityTechnologyRepository,
                capabilityEntityMapper,
                webClient
        );
    }

    @Bean
    public ICapabilityServicePort capabilityServicePort(ICapabilityPersistencePort persistencePort) {
        return new CapabilityUseCase(persistencePort);
    }
}