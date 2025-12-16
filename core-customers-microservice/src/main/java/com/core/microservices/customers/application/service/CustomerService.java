package com.core.microservices.customers.application.service;

import com.core.microservices.customers.application.input.port.CustomerInputPort;
import com.core.microservices.customers.application.output.port.CustomerEventPort;
import com.core.microservices.customers.application.output.port.CustomerOutputPort;
import com.core.microservices.customers.domain.Customer;
import com.core.microservices.customers.infrastructure.exception.BusinessException;
import com.core.microservices.customers.infrastructure.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService implements CustomerInputPort {

    private final CustomerOutputPort customerOutputPort;
    private final CustomerEventPort customerEventPort;

    @Override
    public Mono<Customer> createCustomer(Customer customer) {
        log.info("Creating customer with identification: {}", customer.getIdentification());

        return customerOutputPort.existsByIdentification(customer.getIdentification())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new BusinessException("Customer already exists with identification: " + customer.getIdentification()) {
                        });
                    }
                    return customerOutputPort.save(customer)
                            .flatMap(savedCustomer ->
                                    customerEventPort.publishCustomerCreated(savedCustomer)
                                            .thenReturn(savedCustomer)
                            );
                });
    }

    @Override
    public Mono<Customer> getCustomerById(Long id) {
        return customerOutputPort.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Customer", id)));
    }

    @Override
    public Mono<Customer> getCustomerByIdentification(String identification) {
        return customerOutputPort.findByIdentification(identification)
                .switchIfEmpty(Mono.error(new NotFoundException("Customer", "identification", identification)));
    }

    @Override
    public Flux<Customer> getAllCustomers() {
        return customerOutputPort.findAll();
    }

    @Override
    public Mono<Customer> updateCustomer(Long id, Customer customer) {
        log.info("Updating customer with id: {}", id);

        return customerOutputPort.update(id, customer)
                .flatMap(updatedCustomer ->
                        customerEventPort.publishCustomerUpdated(updatedCustomer)
                                .thenReturn(updatedCustomer)
                );
    }

    @Override
    public Mono<Void> deleteCustomer(Long id) {
        log.info("Deleting customer with id: {}", id);

        return customerOutputPort.deleteById(id)
                .then(customerEventPort.publishCustomerDeleted(id));
    }
}