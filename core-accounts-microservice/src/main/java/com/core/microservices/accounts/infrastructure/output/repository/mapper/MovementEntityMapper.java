package com.core.microservices.accounts.infrastructure.output.repository.mapper;

import com.core.microservices.accounts.domain.Movement;
import com.core.microservices.accounts.infrastructure.output.repository.entity.MovementEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MovementEntityMapper {

    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "customerName", ignore = true)
    Movement toDomain(MovementEntity entity);

    MovementEntity toEntity(Movement domain);
}