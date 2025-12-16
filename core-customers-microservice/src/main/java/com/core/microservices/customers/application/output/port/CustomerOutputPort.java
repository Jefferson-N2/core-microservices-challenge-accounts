package com.core.microservices.customers.application.output.port;

import com.core.microservices.customers.domain.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerOutputPort {
    
    Mono<Customer> save(Customer customer);
    Mono<Customer> findById(Long id);
    Mono<Customer> findByIdentification(String identification);
    Flux<Customer> findAll();
    Mono<Customer> update(Long id, Customer customer);
    Mono<Void> deleteById(Long id);
    Mono<Boolean> existsByIdentification(String identification);
}