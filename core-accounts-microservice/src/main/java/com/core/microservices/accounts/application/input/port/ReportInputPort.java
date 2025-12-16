package com.core.microservices.accounts.application.input.port;

import com.core.microservices.accounts.domain.AccountStatement;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface ReportInputPort {
    
    Mono<AccountStatement> generateReport(Long customerId, LocalDateTime startDate, LocalDateTime endDate);
    Mono<AccountStatement> generateJsonReport(Long customerId, LocalDateTime startDate, LocalDateTime endDate);
}