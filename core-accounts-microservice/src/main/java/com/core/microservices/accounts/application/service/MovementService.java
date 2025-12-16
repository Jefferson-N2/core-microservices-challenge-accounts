package com.core.microservices.accounts.application.service;

import com.core.microservices.accounts.application.input.port.MovementInputPort;
import com.core.microservices.accounts.application.output.port.AccountOutputPort;
import com.core.microservices.accounts.application.output.port.MovementOutputPort;
import com.core.microservices.accounts.domain.Account;
import com.core.microservices.accounts.domain.Movement;
import com.core.microservices.accounts.domain.MovementConstants;
import com.core.microservices.accounts.domain.exception.AccountNotFoundException;
import com.core.microservices.accounts.domain.exception.BusinessException;
import com.core.microservices.accounts.domain.exception.InsufficientBalanceException;
import com.core.microservices.accounts.domain.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.core.microservices.accounts.domain.MovementConstants.CREDIT;
import static com.core.microservices.accounts.domain.MovementConstants.DEBIT;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovementService implements MovementInputPort {

    private final MovementOutputPort movementOutputPort;
    private final AccountOutputPort accountOutputPort;

    @Override
    public Mono<Movement> createMovement(Movement movement) {
        String sanitizedAccountNumber = movement.getAccountNumber() != null ?
                movement.getAccountNumber().replaceAll("[\\r\\n]", "_") : "null";
        log.info("Processing movement: type={}, value={}, account={}",
                movement.getType(), movement.getValue(), sanitizedAccountNumber);

        if (!movement.isValidValue()) {
            return Mono.error(new ValidationException("Movement value must be greater than zero"));
        }

        return accountOutputPort.findByNumber(movement.getAccountNumber())
                .switchIfEmpty(Mono.error(new AccountNotFoundException(movement.getAccountNumber())))
                .flatMap(account -> processMovement(movement, account));
    }

    private Mono<Movement> processMovement(Movement movement, Account account) {

        String type = Optional.ofNullable(movement.getType())
                .map(t -> CREDIT.equalsIgnoreCase(t) ? CREDIT : DEBIT)
                .orElseGet(() -> "");

        return Mono.fromCallable(() ->
                        switch (type) {
                            case CREDIT -> account.credit(movement.getValue());
                            case DEBIT -> account.debit(movement.getValue());
                            default -> throw new ValidationException("Invalid movement type: " + movement.getType());
                        }
                )
                .onErrorResume(InsufficientBalanceException.class, e -> {
                    log.warn("Insufficient balance for account: {}", account.getNumber());
                    return Mono.error(e);
                })
                .flatMap(newBalance -> saveMovementAndUpdateAccount(movement, account, newBalance));
    }

    private Mono<Movement> saveMovementAndUpdateAccount(Movement movement, Account account, BigDecimal newBalance) {
        movement.setAccountId(account.getId());
        movement.setBalance(newBalance);
        movement.setDate(LocalDateTime.now());

        if (movement.getAccountNumber() == null) {
            movement.setAccountNumber(account.getNumber());
        }

        account.setCurrentBalance(newBalance);

        return movementOutputPort.save(movement)
                .flatMap(savedMovement ->
                        accountOutputPort.update(account.getId(), account)
                                .thenReturn(savedMovement)
                );
    }

    @Override
    public Mono<Movement> getMovementById(Long id) {
        return movementOutputPort.findById(id)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(String.valueOf(id))));
    }

    @Override
    public Flux<Movement> getAllMovements() {
        return movementOutputPort.findAll();
    }

    @Override
    public Mono<Movement> updateMovement(Long id, Movement movement) {
        return movementOutputPort.update(id, movement)
                .onErrorMap(DataIntegrityViolationException.class,
                    ex -> new BusinessException(
                        "Cannot update movement: " + ex.getMessage()));
    }

    @Override
    public Mono<Void> deleteMovement(Long id) {
        return movementOutputPort.deleteById(id);
    }
}