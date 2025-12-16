package com.core.microservices.accounts.infrastructure.output.adapter;

import com.core.microservices.accounts.application.output.port.AccountOutputPort;
import com.core.microservices.accounts.application.service.CustomerCacheService;
import com.core.microservices.accounts.domain.Account;
import com.core.microservices.accounts.infrastructure.output.repository.AccountJpaRepository;
import com.core.microservices.accounts.infrastructure.output.repository.entity.AccountEntity;
import com.core.microservices.accounts.infrastructure.output.repository.mapper.AccountEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountAdapter implements AccountOutputPort {

    private final AccountJpaRepository accountJpaRepository;
    private final AccountEntityMapper accountEntityMapper;
    private final CustomerCacheService customerCacheService;

    @Override
    public Mono<Account> save(Account account) {
        return Mono.fromCallable(() -> {
            AccountEntity entity = accountEntityMapper.toEntity(account);
            if (entity.getNumber() == null) {
                entity.setNumber(Account.generateAccountNumber());
            }
            return accountEntityMapper.toDomain(accountJpaRepository.save(entity));
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Account> findById(Long id) {
        return Mono.justOrEmpty(accountJpaRepository.findById(id))
                .map(accountEntityMapper::toDomain)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Account> findByNumber(String number) {
        return Mono.justOrEmpty(accountJpaRepository.findByNumber(number))
                .map(accountEntityMapper::toDomain)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Account> findByCustomerId(Long customerId) {
        return Mono.fromCallable(() -> accountJpaRepository.findByCustomerId(customerId))
                .flatMapMany(list -> Flux.fromIterable(list)
                        .map(accountEntityMapper::toDomain))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Account> findAll() {
        return Mono.fromCallable(accountJpaRepository::findAll)
                .flatMapMany(list -> Flux.fromIterable(list)
                        .map(accountEntityMapper::toDomain))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Account> update(Long id, Account account) {
        return Mono.justOrEmpty(accountJpaRepository.findById(id))
                .map(existing -> {
                    existing.setCurrentBalance(account.getCurrentBalance());
                    existing.setStatus(account.getStatus());
                    existing.setType(account.getType());
                          return accountEntityMapper.toDomain(accountJpaRepository.save(existing));
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return Mono.fromRunnable(() -> accountJpaRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
