package com.core.microservices.accounts.application.input.port;

import com.core.microservices.accounts.domain.Movement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface MovementInputPort {
    
    Mono<Movement> createMovement(Movement movement);
    Mono<Movement> getMovementById(Long id);
    Flux<Movement> getAllMovements();
    Mono<Movement> updateMovement(Long id, Movement movement);
    Mono<Void> deleteMovement(Long id);
}