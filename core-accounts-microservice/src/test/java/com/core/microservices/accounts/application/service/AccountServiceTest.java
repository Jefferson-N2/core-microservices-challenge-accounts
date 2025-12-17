package com.core.microservices.accounts.application.service;

import com.core.microservices.accounts.application.output.port.AccountOutputPort;
import com.core.microservices.accounts.domain.Account;
import com.core.microservices.accounts.domain.CustomerInfo;
import com.core.microservices.accounts.domain.exception.AccountNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
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
class AccountServiceTest {

    @Mock
    private AccountOutputPort accountOutputPort;

    @InjectMocks
    private AccountService accountService;

    @Mock
    private CustomerCacheService customerCacheService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testAccount = Account.builder()
                .id(1L)
                .number("478758")
                .type("Corriente")
                .initialBalance(new BigDecimal("2000.00"))
                .currentBalance(new BigDecimal("2000.00"))
                .status(true)
                .customerId(1L)
                .build();

        CustomerInfo customerInfo = CustomerInfo.builder()
                .customerId(1L).identification("ABC123").name("John Doe").build();

        when(customerCacheService.getCustomerByIdentification(anyString()))
                .thenReturn(Mono.just(customerInfo));

    }

    @Test
    void testGetAccountByIdNotFound() {
        when(accountOutputPort.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(accountService.getAccountById(99999L))
                .expectErrorMatches(throwable -> {
                    assert throwable instanceof AccountNotFoundException :
                            "Expected AccountNotFoundException but got " + throwable.getClass().getSimpleName();
                    return true;
                })
                .verify();
    }

    @Test
    void testGetAllAccounts() {
        when(accountOutputPort.findAll()).thenReturn(Flux.just(testAccount));

        StepVerifier.create(accountService.getAllAccounts())
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testGetAccountByIdSuccessfully() {
        when(accountOutputPort.findById(anyLong())).thenReturn(Mono.just(testAccount));

        StepVerifier.create(accountService.getAccountById(1L))
                .expectNextMatches(account -> {
                    assert account.getId().equals(1L) :
                            "Account ID should be 1 but was " + account.getId();
                    assert account.getNumber().equals("478758") :
                            "Account number should be 478758 but was " + account.getNumber();
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testGetAccountByNumberSuccessfully() {
        when(accountOutputPort.findByNumber(anyString())).thenReturn(Mono.just(testAccount));

        StepVerifier.create(accountService.getAccountByNumber("478758"))
                .expectNextMatches(account -> {
                    assert account.getNumber().equals("478758") :
                            "Account number should be 478758 but was " + account.getNumber();
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testGetAccountByNumberNotFound() {
        when(accountOutputPort.findByNumber(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(accountService.getAccountByNumber("999999"))
                .expectErrorMatches(throwable -> {
                    assert throwable instanceof AccountNotFoundException :
                            "Expected AccountNotFoundException but got " + throwable.getClass().getSimpleName();
                    return true;
                })
                .verify();
    }

    @Test
    void testGetAccountsByCustomerId() {
        when(accountOutputPort.findByCustomerId(anyLong())).thenReturn(Flux.just(testAccount));

        StepVerifier.create(accountService.getAccountsByCustomerId(1L))
                .expectNextMatches(account -> {
                    assert account.getCustomerId().equals(1L) :
                            "Customer ID should be 1 but was " + account.getCustomerId();
                    return true;
                })
                .verifyComplete();
    }


    @Test
    void testDeleteAccountSuccessfully() {
        when(accountOutputPort.deleteById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(accountService.deleteAccount(1L))
                .verifyComplete();

        verify(accountOutputPort, times(1)).deleteById(1L);
    }
}