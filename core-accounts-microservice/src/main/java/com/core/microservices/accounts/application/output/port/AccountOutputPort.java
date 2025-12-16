package com.core.microservices.accounts.application.output.port;

import com.core.microservices.accounts.domain.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountOutputPort {
    
    Mono<Account> save(Account account);
    Mono<Account> findById(Long id);
    Mono<Account> findByNumber(String number);
    Flux<Account> findByCustomerId(Long customerId);
    Flux<Account> findAll();
    Mono<Account> update(Long id, Account account);
    Mono<Void> deleteById(Long id);
}