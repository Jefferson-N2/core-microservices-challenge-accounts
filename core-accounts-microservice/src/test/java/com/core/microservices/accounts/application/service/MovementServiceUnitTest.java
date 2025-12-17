package com.core.microservices.accounts.application.service;

import com.core.microservices.accounts.application.output.port.AccountOutputPort;
import com.core.microservices.accounts.application.output.port.MovementOutputPort;
import com.core.microservices.accounts.domain.Account;
import com.core.microservices.accounts.domain.Movement;
import com.core.microservices.accounts.domain.exception.InsufficientBalanceException;
import com.core.microservices.accounts.domain.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Movement Service Unit Tests - F5 Requirement")
class MovementServiceUnitTest {

    @Mock
    private MovementOutputPort movementOutputPort;

    @Mock
    private AccountOutputPort accountOutputPort;

    private MovementService movementService;

    @BeforeEach
    void setUp() {
        movementService = new MovementService(movementOutputPort, accountOutputPort);
    }

    @Test
    @DisplayName("Should create Credito movement successfully - F2 Business Rule")
    void testCreateCreditMovementSuccessfully() {
        Account account = Account.builder()
                .id(1L)
                .number("478758")
                .currentBalance(new BigDecimal("1000.00"))
                .status(true)
                .build();

        Movement movement = Movement.builder()
                .type("Credito")
                .value(new BigDecimal("500.00"))
                .accountNumber("478758")
                .description("Test credit")
                .build();

        Movement savedMovement = Movement.builder()
                .id(1L)
                .accountId(1L)
                .type("Credito")
                .value(new BigDecimal("500.00"))
                .balance(new BigDecimal("1500.00"))
                .date(LocalDateTime.now())
                .description("Test credit")
                .build();

        when(accountOutputPort.findByNumber(anyString())).thenReturn(Mono.just(account));
        when(movementOutputPort.save(any(Movement.class))).thenReturn(Mono.just(savedMovement));
        when(accountOutputPort.update(any(Long.class), any(Account.class))).thenReturn(Mono.just(account));

        StepVerifier.create(movementService.createMovement(movement))
                .expectNextMatches(result -> 
                    result.getType().equals("Credito") &&
                    result.getValue().equals(new BigDecimal("500.00")) &&
                    result.getBalance().equals(new BigDecimal("1500.00"))
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should reject Debito movement with insufficient balance - F3 Business Rule")
    void testRejectDebitMovementWithInsufficientBalance() {
        Account account = Account.builder()
                .id(1L)
                .number("478758")
                .currentBalance(new BigDecimal("100.00"))
                .status(true)
                .build();

        Movement movement = Movement.builder()
                .type("Debito")
                .value(new BigDecimal("500.00"))
                .accountNumber("478758")
                .description("Large withdrawal")
                .build();

        when(accountOutputPort.findByNumber(anyString())).thenReturn(Mono.just(account));

        StepVerifier.create(movementService.createMovement(movement))
                .expectError(InsufficientBalanceException.class)
                .verify();
    }

    @Test
    @DisplayName("Should reject movement with zero or negative value - F2 Business Rule")
    void testRejectMovementWithInvalidValue() {
        Movement movement = Movement.builder()
                .type("Credito")
                .value(new BigDecimal("-100.00"))
                .accountNumber("478758")
                .description("Invalid movement")
                .build();

        StepVerifier.create(movementService.createMovement(movement))
                .expectErrorMatches(throwable -> 
                    throwable instanceof ValidationException &&
                    throwable.getMessage().contains("Movement value must be greater than zero")
                )
                .verify();
    }
}