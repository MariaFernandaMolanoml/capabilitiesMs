package com.example.capabilities.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Message {

    INTERNAL_ERROR("500", "Something went wrong, please try again", ""),
    INVALID_REQUEST("400", "Invalid request, please verify the data", ""),
    INVALID_PARAMETERS("400", "Invalid parameters, please check", ""),
    INVALID_NAME("403", "Invalid name, please verify", "name"),
    DESCRIPTION_TOO_LONG("403", "Description is too long (max 90 characters)", "description"),
    CAPABILITY_CREATED("201", "Capability created successfully", ""),
    FEW_TECHNOLOGIES("403", "A capability must have at least 3 technologies", ""),
    RANGE_TECHNOLOGIES("403", "A capability must have between 3 and 20 technologies", ""),
    CAPABILITY_ALREADY_EXISTS("400", "This capability already exists", "name"),
    DUPLICATE_TECHNOLOGIES("400", "Duplicate technologies are not allowed", ""),
    INVALID_TECHNOLOGIES("400", "One or more technologies are invalid", "");

    private final String code;
    private final String message;
    private final String param;
}
