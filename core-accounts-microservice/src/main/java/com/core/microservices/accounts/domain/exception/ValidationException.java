package com.core.microservices.accounts.domain.exception;

public class ValidationException extends BusinessException {
    
    public ValidationException(String message) {
        super(message);
    }
}