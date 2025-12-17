package com.core.microservices.accounts.infrastructure.input;

import com.core.microservices.accounts.application.input.port.ReportInputPort;
import com.core.microservices.accounts.infrastructure.input.mapper.ReportDtoMapper;
import com.core.microservices.accounts.infrastructure.input.rest.api.ReportsApi;
import com.core.microservices.accounts.infrastructure.input.rest.dto.ReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class ReportController implements ReportsApi {

    private final ReportInputPort reportInputPort;
    private final ReportDtoMapper reportDtoMapper;

    @Override
    public Mono<ResponseEntity<ReportResponse>> generateReport(
            Long clientId, 
            LocalDate startDate, 
            LocalDate endDate, 
            String format, 
            ServerWebExchange exchange) {
        
        return reportInputPort.generateReport(clientId, startDate.atStartOfDay(), endDate.atTime(23, 59, 59))
            .map(reportDtoMapper::toResponse)
            .map(ResponseEntity::ok);
    }
}