package com.core.microservices.accounts.infrastructure.input;

import com.core.microservices.accounts.domain.Account;
import com.core.microservices.accounts.infrastructure.input.rest.dto.AccountRequest;
import com.core.microservices.accounts.infrastructure.input.rest.dto.AccountResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountDtoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "number", expression = "java(generateAccountNumber(null))")
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "currentBalance", source = "initialBalance", qualifiedByName = "doubleToDecimal")
    @Mapping(target = "status", constant = "true")
    @Mapping(target = "type", source = "type", qualifiedByName = "enumToString")
    Account toDomain(AccountRequest request);

    @Mapping(target = "balance", source = "currentBalance", qualifiedByName = "decimalToDouble")
    AccountResponse toResponse(Account account);
    
    @Named("generateAccountNumber")
    default String generateAccountNumber(Object ignored) {
        return Account.generateAccountNumber();
    }
    
    @Named("enumToString")
    default String enumToString(AccountRequest.TypeEnum type) {
        return type != null ? type.getValue() : null;
    }
    
    @Named("doubleToDecimal")
    default BigDecimal doubleToDecimal(Double value) {

        return value == null ? BigDecimal.valueOf(0.0) : BigDecimal.valueOf(value);
    }
    
    @Named("decimalToDouble")
    default Double decimalToDouble(BigDecimal value) {
        return value == null ? null : value.doubleValue();
    }
    
    @Named("validateIdentification")
    default String validateIdentification(String identification) {
        if (identification == null || identification.trim().isEmpty()) {
            throw new com.core.microservices.accounts.domain.exception.ValidationException(
                "Customer identification is required for account creation");
        }
        return identification.trim();
    }
}