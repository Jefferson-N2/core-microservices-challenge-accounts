package com.core.microservices.customers.infrastructure.exception;

public class NotFoundException extends BusinessException {
    
    public NotFoundException(String entity, String field, Object value) {
        super(String.format("%s not found with %s: %s", entity, field, value));
    }
    
    public NotFoundException(String entity, Long id) {
        super(String.format("%s not found with id: %s", entity, id));
    }
}