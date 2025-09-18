package com.example.capabilities.domain.exceptions;

import com.example.capabilities.domain.enums.Message;
import lombok.Getter;

@Getter
public class ProcessorException extends RuntimeException {

    private final Message technicalMessage;

    public ProcessorException(Throwable cause, Message message) {
        super(cause);
        this.technicalMessage = message;
    }

    public ProcessorException(String message, Message technicalMessage) {
        super(message);
        this.technicalMessage = technicalMessage;
    }
}
