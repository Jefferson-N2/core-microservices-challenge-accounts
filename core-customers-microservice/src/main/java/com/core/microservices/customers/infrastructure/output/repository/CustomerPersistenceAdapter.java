package com.core.microservices.customers.infrastructure.output.repository;

import com.core.microservices.customers.application.output.port.CustomerOutputPort;
import com.core.microservices.customers.domain.Customer;
import com.core.microservices.customers.infrastructure.output.repository.entity.CustomerEntity;
import com.core.microservices.customers.infrastructure.output.repository.entity.PersonEntity;
import com.core.microservices.customers.infrastructure.output.repository.mapper.CustomerEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerPersistenceAdapter implements CustomerOutputPort {

    private final CustomerJpaRepository customerJpaRepository;
    private final PersonJpaRepository personJpaRepository;
    private final CustomerEntityMapper customerEntityMapper;

    @Override
    public Mono<Customer> save(Customer customer) {
        return Mono.fromCallable(() -> {
            CustomerEntity customerEntity = customerEntityMapper.toEntity(customer);
            CustomerEntity savedCustomer = customerJpaRepository.save(customerEntity);
            return customerEntityMapper.toDomain(savedCustomer);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Customer> findById(Long id) {
        return Mono.fromCallable(() -> 
            customerJpaRepository.findById(id)
                .map(customerEntityMapper::toDomain)
        ).flatMap(Mono::justOrEmpty)
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Customer> findByIdentification(String identification) {
        return Mono.fromCallable(() -> 
            customerJpaRepository.findByIdentification(identification)
                .map(customerEntityMapper::toDomain)
        ).flatMap(Mono::justOrEmpty)
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Customer> findAll() {
        return Mono.fromCallable(() -> 
            customerJpaRepository.findAll().stream()
                .map(customerEntityMapper::toDomain)
                .toList()
        ).flatMapMany(Flux::fromIterable)
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Customer> update(Long id, Customer customer) {
        return Mono.fromCallable(() -> {
            return customerJpaRepository.findById(id)
                .map(existing -> {
                    existing.setPassword(customer.getPassword());
                    existing.setStatus(customer.getStatus());
                    
                    if (existing.getPerson() != null) {
                        existing.getPerson().setName(customer.getName());
                        existing.getPerson().setGender(customer.getGender());
                        existing.getPerson().setAddress(customer.getAddress());
                        existing.getPerson().setPhone(customer.getPhone());
                    }
                    
                    CustomerEntity updated = customerJpaRepository.save(existing);
                    return customerEntityMapper.toDomain(updated);
                });
        }).flatMap(Mono::justOrEmpty)
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return Mono.fromRunnable(() -> customerJpaRepository.deleteById(id))
            .subscribeOn(Schedulers.boundedElastic())
            .then();
    }

    @Override
    public Mono<Boolean> existsByIdentification(String identification) {
        return Mono.fromCallable(() -> customerJpaRepository.existsByIdentification(identification))
            .subscribeOn(Schedulers.boundedElastic());
    }
}