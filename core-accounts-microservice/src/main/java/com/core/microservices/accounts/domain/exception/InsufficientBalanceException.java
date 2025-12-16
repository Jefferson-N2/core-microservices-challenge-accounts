package com.core.microservices.accounts.domain.exception;

public class InsufficientBalanceException extends BusinessException {
    
    public InsufficientBalanceException() {
        super("Saldo no disponible");
    }
}