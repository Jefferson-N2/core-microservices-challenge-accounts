package com.core.microservices.accounts.domain.exception;

public class AccountNotFoundException extends BusinessException {
    
    public AccountNotFoundException(String accountNumber) {
        super("Account not found: " + accountNumber);
    }
}