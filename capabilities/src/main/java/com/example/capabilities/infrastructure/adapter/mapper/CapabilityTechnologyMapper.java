package com.example.capabilities.infrastructure.adapter.mapper;

import com.example.capabilities.domain.model.CapabilityTechnology;
import com.example.capabilities.infrastructure.adapter.entity.CapabilityTechnologyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CapabilityTechnologyMapper {
    CapabilityTechnology toModel(CapabilityTechnologyEntity entity);
    CapabilityTechnologyEntity toEntity(CapabilityTechnology model);
}
