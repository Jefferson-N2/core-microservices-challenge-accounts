package com.core.microservices.accounts.application.output.port;

import com.core.microservices.accounts.domain.AccountStatement;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Mono;

public interface ReportOutputPort {
    
    Mono<Resource> generateExcelFile(AccountStatement statement);
}