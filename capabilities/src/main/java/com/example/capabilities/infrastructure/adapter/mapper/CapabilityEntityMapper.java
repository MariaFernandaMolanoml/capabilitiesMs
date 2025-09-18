package com.example.capabilities.infrastructure.adapter.mapper;


import com.example.capabilities.domain.model.Capability;
import com.example.capabilities.infrastructure.adapter.entity.CapabilityEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CapabilityEntityMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    Capability toModel(CapabilityEntity entity);
    CapabilityEntity toEntity(Capability model);
}