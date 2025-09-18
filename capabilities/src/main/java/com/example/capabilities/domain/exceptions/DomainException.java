package com.example.capabilities.domain.exceptions;

import com.example.capabilities.domain.enums.Message;
import lombok.Getter;

@Getter
public class DomainException extends ProcessorException {

    public DomainException(Message technicalMessage) {
        super(technicalMessage.getMessage(), technicalMessage);
    }
}