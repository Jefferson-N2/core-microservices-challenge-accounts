package com.core.microservices.accounts.infrastructure.input;

import com.core.microservices.accounts.domain.AccountStatement;
import com.core.microservices.accounts.infrastructure.input.rest.dto.ReportResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReportDtoMapper {
    
    ReportResponse toResponse(AccountStatement statement);
    
    default OffsetDateTime map(LocalDateTime value) {
        return value == null ? null : value.atOffset(ZoneOffset.UTC);
    }
}