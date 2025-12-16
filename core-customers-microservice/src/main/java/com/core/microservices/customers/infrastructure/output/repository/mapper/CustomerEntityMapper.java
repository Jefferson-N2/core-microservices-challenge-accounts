package com.core.microservices.customers.infrastructure.output.repository.mapper;

import com.core.microservices.customers.domain.Customer;
import com.core.microservices.customers.infrastructure.output.repository.entity.CustomerEntity;
import com.core.microservices.customers.infrastructure.output.repository.entity.PersonEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerEntityMapper {

    @Mapping(source = "id", target = "customerId")
    @Mapping(source = "person.id", target = "id")
    @Mapping(source = "person.name", target = "name")
    @Mapping(source = "person.gender", target = "gender")
    @Mapping(source = "person.identification", target = "identification")
    @Mapping(source = "person.address", target = "address")
    @Mapping(source = "person.phone", target = "phone")
    Customer toDomain(CustomerEntity entity);

    @Mapping(source = "customerId", target = "id")
    @Mapping(target = "person", source = ".")
    CustomerEntity toEntity(Customer domain);

    @Mapping(source = "id", target = "id")
    PersonEntity toPersonEntity(Customer customer);
}