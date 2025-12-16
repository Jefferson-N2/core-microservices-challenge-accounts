package com.core.microservices.customers.infrastructure.exception;

public class ConflictException extends RuntimeException {

  public ConflictException(String message) {
    super(message);
  }
} 