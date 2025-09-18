package com.example.capabilities.infrastructure.entrypoints.mapper;

import com.example.capabilities.domain.model.Capability;
import com.example.capabilities.infrastructure.entrypoints.dto.CapabilityDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CapabilityMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    Capability dtoToModel(CapabilityDTO dto);
    CapabilityDTO modelToDto(Capability model);
}