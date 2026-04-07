package com.example.capabilities.infrastructure.entrypoints.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para Capability")
public class CapabilityDTO {
    @Schema(description = "ID de la capability", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID id;

    @Schema(description = "Nombre de la capability", example = "DevOps")
    private String name;

    @Schema(description = "Descripción de la capability", example = "Automatización y CI/CD")
    private String description;

    @Schema(description = "IDs de tecnologías asociadas")
    private List<UUID> technologies;
}
