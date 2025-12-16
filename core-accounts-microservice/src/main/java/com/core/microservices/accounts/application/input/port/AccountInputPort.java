package com.core.microservices.accounts.application.input.port;

import com.core.microservices.accounts.domain.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountInputPort {
    
    Mono<Account> createAccount(Account account);
    Mono<Account> getAccountById(Long id);
    Mono<Account> getAccountByNumber(String number);
    Flux<Account> getAccountsByCustomerId(Long customerId);
    Flux<Account> getAllAccounts();
    Mono<Account> updateAccount(Long id, Account account);
    Mono<Void> deleteAccount(Long id);
}