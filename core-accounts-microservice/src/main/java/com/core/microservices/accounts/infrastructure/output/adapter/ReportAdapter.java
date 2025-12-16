package com.core.microservices.accounts.infrastructure.output.adapter;

import com.core.microservices.accounts.application.output.port.ReportOutputPort;
import com.core.microservices.accounts.domain.AccountStatement;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ReportAdapter implements ReportOutputPort {

    @Override
    public Mono<Resource> generateExcelFile(AccountStatement statement) {
        String content = "Account Statement\n" + statement.toString();
        byte[] bytes = content.getBytes();
        return Mono.just(new ByteArrayResource(bytes));
    }
}