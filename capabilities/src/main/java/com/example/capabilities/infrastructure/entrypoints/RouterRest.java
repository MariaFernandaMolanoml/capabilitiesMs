package com.example.capabilities.infrastructure.entrypoints;

import com.example.capabilities.infrastructure.entrypoints.handler.ICapabilityHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

    @Configuration
    public class RouterRest {
        @Bean
        public RouterFunction<ServerResponse> capabilityRoutes(ICapabilityHandler handler) {
            return RouterFunctions.route(POST("/capabilities"), handler::createCapability)
                    .andRoute(GET("/capabilities"), handler::listCapabilities)
                    .andRoute(GET("/capabilities/simple"), handler::listCapabilitiesSimple);
        }
    }