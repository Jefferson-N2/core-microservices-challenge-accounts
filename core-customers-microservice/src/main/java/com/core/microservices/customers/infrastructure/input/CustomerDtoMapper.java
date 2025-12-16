package com.core.microservices.customers.infrastructure.input;

import com.core.microservices.customers.domain.Customer;
import com.core.microservices.customers.infrastructure.input.rest.dto.CustomerRequest;
import com.core.microservices.customers.infrastructure.input.rest.dto.CustomerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerDtoMapper {

    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "id", ignore = true)
    Customer toDomain(CustomerRequest request);
    
    @Mapping(source = "customerId", target = "id")
    CustomerResponse toResponse(Customer customer);
}