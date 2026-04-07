package com.example.capabilities.infrastructure.entrypoints.handler;

import com.example.capabilities.domain.api.ICapabilityServicePort;
import com.example.capabilities.domain.model.Capability;
import com.example.capabilities.domain.spi.ICapabilityPersistencePort;
import com.example.capabilities.infrastructure.entrypoints.dto.CapabilityDTO;
import com.example.capabilities.infrastructure.entrypoints.dto.CapabilityListDTO;
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
    private final ICapabilityPersistencePort capabilityPersistencePort;

    @Override
    public Mono<ServerResponse> createCapability(ServerRequest request) {
        return request.bodyToMono(CapabilityDTO.class)
                .flatMap(dto -> {
                    Capability capability = capabilityMapper.dtoToModel(dto);
                    List<UUID> technologies = dto.getTechnologies();
                    return capabilityServicePort.registerCapability(capability, technologies);
                })
                .flatMap(saved -> ServerResponse.status(201).bodyValue(saved));
    }

    @Override
    public Mono<ServerResponse> listCapabilities(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        String sortBy = request.queryParam("sortBy").orElse("name");
        String order = request.queryParam("order").orElse("asc");

        return ServerResponse.ok()
                .body(
                        capabilityServicePort.listCapabilities(page, size, sortBy, order)
                                .map(capabilityMapper::toDto),
                        CapabilityListDTO.class
                );
    }
    @Override
    public Mono<ServerResponse> listCapabilitiesSimple(ServerRequest request) {
        return ServerResponse.ok()
                .body(
                        capabilityServicePort.listAllCapabilities()
                                .map(capabilityMapper::modelToDto),
                        CapabilityDTO.class
                );
    }
}
