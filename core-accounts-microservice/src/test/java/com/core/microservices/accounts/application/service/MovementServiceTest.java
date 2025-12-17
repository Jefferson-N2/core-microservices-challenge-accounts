package com.core.microservices.accounts.application.service;

import com.core.microservices.accounts.application.output.port.AccountOutputPort;
import com.core.microservices.accounts.application.output.port.MovementOutputPort;
import com.core.microservices.accounts.domain.Account;
import com.core.microservices.accounts.domain.Movement;
import com.core.microservices.accounts.domain.MovementConstants;
import com.core.microservices.accounts.domain.exception.InsufficientBalanceException;
import com.core.microservices.accounts.domain.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MovementServiceTest {

    @Mock
    private MovementOutputPort movementOutputPort;

    @Mock
    private AccountOutputPort accountOutputPort;

    @InjectMocks
    private MovementService movementService;

    private Account testAccount;
    private Movement testMovement;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
            .id(1L)
            .number("478758")
            .type("Corriente")
            .initialBalance(new BigDecimal("2000.00"))
            .currentBalance(new BigDecimal("2000.00"))
            .status(true)
            .customerId(1L)
            .build();

        testMovement = Movement.builder()
            .type(MovementConstants.DEBIT)
            .value(new BigDecimal("575.00"))
            .accountNumber("478758")
            .description("Retiro de 575")
            .build();
    }

    @Test
    void testCreateMovementWithValidDebitValue() {
        when(accountOutputPort.findByNumber(anyString())).thenReturn(Mono.just(testAccount));
        when(movementOutputPort.save(any(Movement.class))).thenReturn(Mono.just(testMovement));
        when(accountOutputPort.update(anyLong(), any(Account.class))).thenReturn(Mono.just(testAccount));

        StepVerifier.create(movementService.createMovement(testMovement))
            .expectNextMatches(movement -> {
                assert movement.getValue().equals(new BigDecimal("575.00")) : 
                    "Movement value should be 575.00 but was " + movement.getValue();
                assert movement.getType().equals(MovementConstants.DEBIT) :
                    "Movement type should be Debito but was " + movement.getType();
                return true;
            })
            .verifyComplete();
    }

    @Test
    void testCreateMovementWithInsufficientBalance() {
        testAccount.setCurrentBalance(new BigDecimal("100.00"));
        testMovement.setValue(new BigDecimal("575.00"));
        
        when(accountOutputPort.findByNumber(anyString())).thenReturn(Mono.just(testAccount));

        StepVerifier.create(movementService.createMovement(testMovement))
            .expectErrorMatches(throwable -> {
                assert throwable instanceof InsufficientBalanceException : 
                    "Expected InsufficientBalanceException but got " + throwable.getClass().getSimpleName();
                assert "Saldo no disponible".equals(throwable.getMessage()) : 
                    "Expected message 'Saldo no disponible' but got '" + throwable.getMessage() + "'";
                return true;
            })
            .verify();
    }

    @Test
    void testCreateMovementWithZeroValue() {
        Movement invalidMovement = Movement.builder()
            .type(MovementConstants.DEBIT)
            .value(BigDecimal.ZERO)
            .accountNumber("478758")
            .description("Invalid movement")
            .build();

        StepVerifier.create(Mono.fromCallable(() -> movementService.createMovement(invalidMovement).block()))
            .expectErrorMatches(throwable -> {
                assert throwable instanceof ValidationException : 
                    "Expected ValidationException but got " + throwable.getClass().getSimpleName();
                assert throwable.getMessage().contains("greater than zero") : 
                    "Expected validation message about 'greater than zero' but got '" + throwable.getMessage() + "'";
                return true;
            })
            .verify();
    }

    @Test
    void testCreateMovementWithCreditType() {
        testMovement.setType(MovementConstants.CREDIT);
        testMovement.setValue(new BigDecimal("600.00"));
        
        when(accountOutputPort.findByNumber(anyString())).thenReturn(Mono.just(testAccount));
        when(movementOutputPort.save(any(Movement.class))).thenReturn(Mono.just(testMovement));
        when(accountOutputPort.update(anyLong(), any(Account.class))).thenReturn(Mono.just(testAccount));

        StepVerifier.create(movementService.createMovement(testMovement))
            .expectNextMatches(movement -> {
                assert movement.getType().equals(MovementConstants.CREDIT) :
                    "Movement type should be Credito but was " + movement.getType();
                assert movement.getValue().equals(new BigDecimal("600.00")) : 
                    "Movement value should be 600.00 but was " + movement.getValue();
                return true;
            })
            .verifyComplete();
    }

    @Test
    void testCreateMovementWithNegativeValue() {
        Movement invalidMovement = Movement.builder()
            .type(MovementConstants.DEBIT)
            .value(new BigDecimal("-100.00"))
            .accountNumber("478758")
            .description("Invalid movement")
            .build();

        StepVerifier.create(Mono.fromCallable(() -> movementService.createMovement(invalidMovement).block()))
            .expectErrorMatches(throwable -> {
                assert throwable instanceof ValidationException : 
                    "Expected ValidationException but got " + throwable.getClass().getSimpleName();
                assert throwable.getMessage().contains("greater than zero") : 
                    "Expected validation message about 'greater than zero' but got '" + throwable.getMessage() + "'";
                return true;
            })
            .verify();
    }

    @Test
    void testCreateMovementAccountNotFound() {
        when(accountOutputPort.findByNumber(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(movementService.createMovement(testMovement))
            .expectErrorMatches(throwable -> {
                assert throwable instanceof com.core.microservices.accounts.domain.exception.AccountNotFoundException : 
                    "Expected AccountNotFoundException but got " + throwable.getClass().getSimpleName();
                return true;
            })
            .verify();
    }

    @Test
    void testCreateMovementBalanceCalculation() {
        
        testAccount.setCurrentBalance(new BigDecimal("1000.00"));
        Movement debitMovement = Movement.builder()
            .type(MovementConstants.DEBIT)
            .value(new BigDecimal("200.00"))
            .accountNumber("478758")
            .description("Balance calculation test")
            .build();
        
        when(accountOutputPort.findByNumber(anyString())).thenReturn(Mono.just(testAccount));
        when(movementOutputPort.save(any(Movement.class))).thenReturn(Mono.just(debitMovement));
        when(accountOutputPort.update(anyLong(), any(Account.class))).thenReturn(Mono.just(testAccount));

        StepVerifier.create(movementService.createMovement(debitMovement))
            .expectNextMatches(movement -> {
                assert movement.getValue().equals(new BigDecimal("200.00")) : 
                    "Movement value should be 200.00 but was " + movement.getValue();
                assert movement.getType().equals(MovementConstants.DEBIT) :
                    "Movement type should be Debito but was " + movement.getType();
                return true;
            })
            .verifyComplete();
        
        verify(accountOutputPort, times(1)).update(anyLong(), any(Account.class));
    }
}