package com.example.capabilities.infrastructure.entrypoints.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CapabilityListDTO {
    private UUID id;
    private String name;
    private List<TechnologySummaryDTO> technologies;
}