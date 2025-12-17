package com.core.microservices.accounts.infrastructure.input.mapper;

import com.core.microservices.accounts.domain.Movement;
import com.core.microservices.accounts.infrastructure.input.rest.dto.MovementRequest;
import com.core.microservices.accounts.infrastructure.input.rest.dto.MovementResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MovementDtoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "customerName", ignore = true)
    @Mapping(target = "accountNumber", source = "numberAccount")
    Movement toDomain(MovementRequest request);
    
    @Mapping(target = "accountNumber", source = "accountNumber")
    MovementResponse toResponse(Movement movement);

    default OffsetDateTime map(LocalDateTime value) {
        return value == null ? null : value.atOffset(ZoneOffset.UTC);
    }
}