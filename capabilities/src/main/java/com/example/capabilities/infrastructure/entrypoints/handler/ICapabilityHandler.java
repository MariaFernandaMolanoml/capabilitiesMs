package com.example.capabilities.infrastructure.entrypoints.handler;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface ICapabilityHandler {
    Mono<ServerResponse> createCapability(ServerRequest request);
}
