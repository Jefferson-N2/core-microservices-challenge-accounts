package com.core.microservices.accounts.application.output.port;

import com.core.microservices.accounts.domain.Movement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface MovementOutputPort {
    
    Mono<Movement> save(Movement movement);
    Mono<Movement> findById(Long id);
    Flux<Movement> findByAccountId(Long accountId);
    Flux<Movement> findAll();
    Flux<Movement> findByAccountIdAndDateBetween(Long accountId, LocalDateTime startDate, LocalDateTime endDate);
    Mono<Movement> update(Long id, Movement movement);
    Mono<Void> deleteById(Long id);
}