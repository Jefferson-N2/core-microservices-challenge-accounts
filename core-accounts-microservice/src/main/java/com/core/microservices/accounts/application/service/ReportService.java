package com.core.microservices.accounts.application.service;

import com.core.microservices.accounts.application.input.port.ReportInputPort;
import com.core.microservices.accounts.application.output.port.AccountOutputPort;
import com.core.microservices.accounts.application.output.port.ReportOutputPort;
import com.core.microservices.accounts.domain.AccountStatement;
import com.core.microservices.accounts.domain.CustomerInfo;
import com.core.microservices.accounts.application.output.port.MovementOutputPort;
import com.core.microservices.accounts.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService implements ReportInputPort {

    private final AccountOutputPort accountOutputPort;
    private final MovementOutputPort movementOutputPort;
    private final ReportOutputPort reportOutputPort;
    private final CustomerCacheService customerCacheService;

    @Override
    public Mono<AccountStatement> generateReport(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        return generateJsonReport(customerId, startDate, endDate);
    }

    @Override
    public Mono<AccountStatement> generateJsonReport(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating JSON report for customer: {} from {} to {}", customerId, startDate, endDate);

        return accountOutputPort.findByCustomerId(customerId)
                .collectList()
                .flatMap(accounts -> {
                    if (accounts.isEmpty()) {
                        return Mono.error(new BusinessException(
                                "No accounts found for customer: " + customerId));
                    }

                    return customerCacheService.getCustomerInfo(customerId)
                            .defaultIfEmpty(CustomerInfo.builder()
                                    .customerId(customerId)
                                    .name("Customer " + customerId)
                                    .build())
                            .flatMap(customerInfo -> {
                                AccountStatement statement = AccountStatement.builder()
                                        .clientId(customerId)
                                        .clientName(customerInfo.getName())
                                        .startDate(startDate.toLocalDate())
                                        .endDate(endDate.toLocalDate())
                                        .build();

                                return Flux.fromIterable(accounts)
                                        .flatMap(account ->
                                                movementOutputPort.findByAccountIdAndDateBetween(account.getId(), startDate, endDate)
                                                        .collectList()
                                                        .map(movements -> AccountStatement.AccountSummary.builder()
                                                                .accountNumber(account.getNumber())
                                                                .accountType(account.getType())
                                                                .initialBalance(account.getInitialBalance() != null ?
                                                                        account.getInitialBalance().doubleValue() : 0.0)
                                                                .currentBalance(account.getCurrentBalance() != null ?
                                                                        account.getCurrentBalance().doubleValue() : 0.0)
                                                                .movements(movements)
                                                                .build())
                                        )
                                        .collectList()
                                        .map(accountSummaries -> {
                                            statement.setAccounts(accountSummaries);
                                            return statement;
                                        });
                            });
                });
    }

}