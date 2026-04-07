package com.example.capabilities.infrastructure.entrypoints;

import com.example.capabilities.infrastructure.entrypoints.dto.CapabilityDTO;
import com.example.capabilities.infrastructure.entrypoints.dto.CapabilityListDTO;
import com.example.capabilities.infrastructure.entrypoints.handler.CapabilityHandlerImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({

            @RouterOperation(
                    path = "/capabilities",
                    method = RequestMethod.POST,
                    beanClass = CapabilityHandlerImpl.class,
                    beanMethod = "createCapability",
                    operation = @Operation(
                            operationId = "createCapability",
                            summary = "Crear una nueva capacidad",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    description = "Datos de la capacidad a crear",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = CapabilityDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Capacidad creada correctamente",
                                            content = @Content(schema = @Schema(implementation = CapabilityDTO.class))),
                                    @ApiResponse(responseCode = "400", description = "Error de validación"),
                                    @ApiResponse(responseCode = "500", description = "Error interno")
                            }
                    )
            ),

            @RouterOperation(
                    path = "/capabilities",
                    method = RequestMethod.GET,
                    beanClass = CapabilityHandlerImpl.class,
                    beanMethod = "listCapabilities",
                    operation = @Operation(
                            operationId = "listCapabilities",
                            summary = "Listar capacidades con paginación",
                            parameters = {
                                    @Parameter(name = "page", description = "Número de página", required = false),
                                    @Parameter(name = "size", description = "Tamaño de página", required = false),
                                    @Parameter(name = "sortBy", description = "Campo para ordenar", required = false),
                                    @Parameter(name = "order", description = "Orden asc o desc", required = false)
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Lista de capacidades",
                                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CapabilityListDTO.class)))),
                                    @ApiResponse(responseCode = "500", description = "Error interno")
                            }
                    )
            ),

            @RouterOperation(
                    path = "/capabilities/simple",
                    method = RequestMethod.GET,
                    beanClass = CapabilityHandlerImpl.class,
                    beanMethod = "listCapabilitiesSimple",
                    operation = @Operation(
                            operationId = "listCapabilitiesSimple",
                            summary = "Listar capacidades en formato simple",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Lista de capacidades simples",
                                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CapabilityDTO.class)))),
                                    @ApiResponse(responseCode = "500", description = "Error interno")
                            }
                    )
            )

    })
    public RouterFunction<ServerResponse> capabilityRoutes(CapabilityHandlerImpl handler) {
        return RouterFunctions.route(POST("/capabilities"), handler::createCapability)
                .andRoute(GET("/capabilities"), handler::listCapabilities)
                .andRoute(GET("/capabilities/simple"), handler::listCapabilitiesSimple);
    }
}
