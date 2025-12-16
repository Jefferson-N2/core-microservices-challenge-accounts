package com.core.microservices.customers.application.output.port;

import com.core.microservices.customers.domain.Customer;
import reactor.core.publisher.Mono;

public interface CustomerEventPort {
    
    Mono<Void> publishCustomerCreated(Customer customer);
    Mono<Void> publishCustomerUpdated(Customer customer);
    Mono<Void> publishCustomerDeleted(Long customerId);
}