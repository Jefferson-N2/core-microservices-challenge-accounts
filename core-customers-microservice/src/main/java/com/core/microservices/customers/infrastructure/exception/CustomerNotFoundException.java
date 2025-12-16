package com.core.microservices.customers.infrastructure.exception;

public class CustomerNotFoundException extends RuntimeException {
    
    public CustomerNotFoundException(String message) {
        super(message);
    }
    
    public CustomerNotFoundException(Long id) {
        super("Customer not found with id: " + id);
    }
    
    public CustomerNotFoundException(String field, String value) {
        super("Customer not found with " + field + ": " + value);
    }
}