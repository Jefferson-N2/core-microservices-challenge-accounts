package com.core.microservices.customers.infrastructure.input;

import com.core.microservices.customers.application.input.port.CustomerInputPort;
import com.core.microservices.customers.infrastructure.input.rest.api.CustomersApi;
import com.core.microservices.customers.infrastructure.input.rest.dto.CustomerRequest;
import com.core.microservices.customers.infrastructure.input.rest.dto.CustomerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomersApi {

    private final CustomerInputPort customerInputPort;
    private final CustomerDtoMapper customerDtoMapper;

    @Override
    public Mono<ResponseEntity<CustomerResponse>> createCustomer(Mono<CustomerRequest> customerRequest, ServerWebExchange exchange) {
        return customerRequest
            .map(customerDtoMapper::toDomain)
            .flatMap(customerInputPort::createCustomer)
            .map(customerDtoMapper::toResponse)
            .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @Override
    public Mono<ResponseEntity<CustomerResponse>> getCustomerById(Long id, ServerWebExchange exchange) {
        return customerInputPort.getCustomerById(id)
            .map(customerDtoMapper::toResponse)
            .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<CustomerResponse>>> getAllCustomers(ServerWebExchange exchange) {
        Flux<CustomerResponse> customers = customerInputPort.getAllCustomers()
            .map(customerDtoMapper::toResponse);
        return Mono.just(ResponseEntity.ok(customers));
    }

    @Override
    public Mono<ResponseEntity<CustomerResponse>> updateCustomer(Long id, Mono<CustomerRequest> customerRequest, ServerWebExchange exchange) {
        return customerRequest
            .map(customerDtoMapper::toDomain)
            .flatMap(customer -> customerInputPort.updateCustomer(id, customer))
            .map(customerDtoMapper::toResponse)
            .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteCustomer(Long id, ServerWebExchange exchange) {
        return customerInputPort.deleteCustomer(id)
            .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
    
    @Override
    public Mono<ResponseEntity<CustomerResponse>> getCustomerByIdentification(String identification, ServerWebExchange exchange) {
        return customerInputPort.getCustomerByIdentification(identification)
            .map(customerDtoMapper::toResponse)
            .map(ResponseEntity::ok);
    }
}