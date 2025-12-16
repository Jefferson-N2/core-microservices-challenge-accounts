package com.core.microservices.accounts.infrastructure.output.adapter;

import com.core.microservices.accounts.application.output.port.MovementOutputPort;
import com.core.microservices.accounts.domain.Movement;
import com.core.microservices.accounts.domain.exception.BusinessException;
import com.core.microservices.accounts.infrastructure.output.repository.AccountJpaRepository;
import com.core.microservices.accounts.infrastructure.output.repository.MovementJpaRepository;
import com.core.microservices.accounts.infrastructure.output.repository.mapper.MovementEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MovementAdapter implements MovementOutputPort {

    private final MovementJpaRepository movementRepository;
    private final AccountJpaRepository accountRepository;
    private final MovementEntityMapper movementEntityMapper;

    @Override
    public Mono<Movement> save(Movement movement) {
        return Mono.fromCallable(() -> movementEntityMapper.toEntity(movement))
                .flatMap(entity -> Mono.fromCallable(() -> movementRepository.save(entity)))
                .map(movementEntityMapper::toDomain)
                .map(entity -> {
                    accountRepository.findById(entity.getAccountId())
                            .ifPresent(account -> entity.setAccountNumber(account.getNumber()));
                    return entity;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Movement> findById(Long id) {
        return Mono.fromCallable(() -> movementRepository.findById(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(entity -> {
                    Movement movement = movementEntityMapper.toDomain(entity);
                    accountRepository.findById(entity.getAccountId())
                            .ifPresent(account -> movement.setAccountNumber(account.getNumber()));
                    return movement;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Movement> findByAccountId(Long accountId) {
        return Flux.fromIterable(movementRepository.findByAccountId(accountId))
                .map(entity -> {
                    Movement movement = movementEntityMapper.toDomain(entity);
                    accountRepository.findById(entity.getAccountId())
                            .ifPresent(account -> movement.setAccountNumber(account.getNumber()));
                    return movement;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Movement> findAll() {
        return Flux.fromIterable(movementRepository.findAll())
                .map(entity -> {
                    Movement movement = movementEntityMapper.toDomain(entity);
                    accountRepository.findById(entity.getAccountId())
                            .ifPresent(account -> movement.setAccountNumber(account.getNumber()));
                    return movement;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Movement> findByAccountIdAndDateBetween(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        return Flux.fromIterable(movementRepository.findByAccountIdAndDateBetween(accountId, startDate, endDate))
                .map(movementEntityMapper::toDomain)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Movement> update(Long id, Movement movement) {
        return findById(id)
                .switchIfEmpty(Mono.error(new BusinessException("Movement not found with id: " + id)))
                .map(existing -> {
                    movement.setId(id);
                    movement.setAccountId(existing.getAccountId());
                    if (movement.getDate() == null) {
                        movement.setDate(existing.getDate()); 
                    }
                    return movement;
                })
                .flatMap(this::save)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return Mono.fromRunnable(() -> movementRepository.deleteById(id));
    }
}