package com.core.microservices.customers.application.input.port;

import com.core.microservices.customers.domain.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerInputPort {
    
    Mono<Customer> createCustomer(Customer customer);
    Mono<Customer> getCustomerById(Long id);
    Mono<Customer> getCustomerByIdentification(String identification);
    Flux<Customer> getAllCustomers();
    Mono<Customer> updateCustomer(Long id, Customer customer);
    Mono<Void> deleteCustomer(Long id);
}