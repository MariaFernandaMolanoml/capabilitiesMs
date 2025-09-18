package com.example.capabilities.infrastructure.adapter.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("capability_technology")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapabilityTechnologyEntity {
    @Id
    private UUID id;
    private UUID capabilityId;
    private UUID technologyId;
}