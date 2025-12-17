package com.core.microservices.accounts.infrastructure.input;

import com.core.microservices.accounts.application.input.port.MovementInputPort;
import com.core.microservices.accounts.infrastructure.input.mapper.MovementDtoMapper;
import com.core.microservices.accounts.infrastructure.input.rest.api.MovementsApi;
import com.core.microservices.accounts.infrastructure.input.rest.dto.MovementRequest;
import com.core.microservices.accounts.infrastructure.input.rest.dto.MovementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class MovementController implements MovementsApi {

    private final MovementInputPort movementInputPort;
    private final MovementDtoMapper movementDtoMapper;

    @Override
    public Mono<ResponseEntity<Flux<MovementResponse>>> getAllMovements(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(
            movementInputPort.getAllMovements().map(movementDtoMapper::toResponse)
        ));
    }

    @Override
    public Mono<ResponseEntity<MovementResponse>> createMovement(Mono<MovementRequest> movementRequest, ServerWebExchange exchange) {
        return movementRequest
            .map(movementDtoMapper::toDomain)
            .flatMap(movementInputPort::createMovement)
            .map(movementDtoMapper::toResponse)
            .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @Override
    public Mono<ResponseEntity<MovementResponse>> getMovementById(Long id, ServerWebExchange exchange) {
        return movementInputPort.getMovementById(id)
            .map(movementDtoMapper::toResponse)
            .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<MovementResponse>> updateMovement(Long id, Mono<MovementRequest> movementRequest, ServerWebExchange exchange) {
        return movementRequest
            .map(movementDtoMapper::toDomain)
            .flatMap(movement -> movementInputPort.updateMovement(id, movement))
            .map(movementDtoMapper::toResponse)
            .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteMovement(Long id, ServerWebExchange exchange) {
        return movementInputPort.deleteMovement(id)
            .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}