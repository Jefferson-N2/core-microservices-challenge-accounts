package com.core.microservices.accounts.infrastructure.output.repository.mapper;

import com.core.microservices.accounts.domain.Account;
import com.core.microservices.accounts.infrastructure.output.repository.entity.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountEntityMapper {

    @Mapping(target = "currentBalance", source = "currentBalance")
    Account toDomain(AccountEntity entity);

    @Mapping(target = "customerId", source = "customerId")
    AccountEntity toEntity(Account domain);
}