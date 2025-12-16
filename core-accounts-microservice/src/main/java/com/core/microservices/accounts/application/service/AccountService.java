package com.core.microservices.accounts.application.service;

import com.core.microservices.accounts.application.input.port.AccountInputPort;
import com.core.microservices.accounts.application.output.port.AccountOutputPort;
import com.core.microservices.accounts.domain.Account;
import com.core.microservices.accounts.domain.CustomerInfo;
import com.core.microservices.accounts.domain.exception.AccountNotFoundException;
import com.core.microservices.accounts.domain.exception.BusinessException;
import com.core.microservices.accounts.infrastructure.input.event.CustomerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService implements AccountInputPort {

    private final AccountOutputPort accountOutputPort;
    private final CustomerCacheService customerCacheService;

    @Override
    public Mono<Account> createAccount(Account account) {
        String identification = account.getIdentification();

        log.info("Creating account for customer with identification: {}", identification);

        return customerCacheService.getCustomerByIdentification(identification)
                .switchIfEmpty(Mono.error(new BusinessException("Customer not found with identification: " + identification)))
                .map(customerInfo -> {
                    account.setCustomerId(customerInfo.getId());
                    return account;
                })
                .flatMap(accountOutputPort::save);
    }

    @Override
    public Mono<Account> getAccountById(Long id) {
        return accountOutputPort.findById(id)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(String.valueOf(id))));
    }

    @Override
    public Mono<Account> getAccountByNumber(String number) {
        return accountOutputPort.findByNumber(number)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(number)));
    }

    @Override
    public Flux<Account> getAccountsByCustomerId(Long customerId) {
        return accountOutputPort.findByCustomerId(customerId);
    }

    @Override
    public Flux<Account> getAllAccounts() {
        return accountOutputPort.findAll();
    }

    @Override
    public Mono<Account> updateAccount(Long id, Account account) {
        String identification = account.getIdentification();
        log.info("Updating account with id: {} for customer with identification: {}", id, identification);

        return accountOutputPort.findById(id)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(String.valueOf(id))))
                .then(customerCacheService.getCustomerByIdentification(identification)
                        .switchIfEmpty(Mono.error(new BusinessException("Customer not found with identification: " + identification))))
                .map(customerInfo -> {
                    account.setCustomerId(customerInfo.getId());
                    return account;
                })
                .flatMap(acc -> accountOutputPort.update(id, acc));
    }


    @Override
    public Mono<Void> deleteAccount(Long id) {
        log.info("Deleting account with id: {}", id);
        return accountOutputPort.deleteById(id);
    }
}
