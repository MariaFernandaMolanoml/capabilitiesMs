package com.example.capabilities.infrastructure.entrypoints.handler;

import com.example.capabilities.domain.api.ICapabilityServicePort;
import com.example.capabilities.domain.model.Capability;
import com.example.capabilities.infrastructure.entrypoints.dto.CapabilityDTO;
import com.example.capabilities.infrastructure.entrypoints.mapper.CapabilityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CapabilityHandlerImpl implements ICapabilityHandler {

    private final ICapabilityServicePort capabilityServicePort;
    private final CapabilityMapper capabilityMapper;

    @Override
    public Mono<ServerResponse> createCapability(ServerRequest request) {
        return request.bodyToMono(CapabilityDTO.class)
                .flatMap(dto -> {
                    Capability capability = capabilityMapper.dtoToModel(dto);
                    List<UUID> technologies = dto.getTechnologies(); // solo IDs
                    return capabilityServicePort.registerCapability(capability, technologies);
                })
                .flatMap(saved -> ServerResponse.ok().bodyValue(saved));
    }
}
