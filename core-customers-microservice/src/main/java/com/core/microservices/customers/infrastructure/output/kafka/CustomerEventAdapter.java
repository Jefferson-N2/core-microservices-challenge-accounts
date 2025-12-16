package com.core.microservices.customers.infrastructure.output.kafka;

import com.core.microservices.customers.application.output.port.CustomerEventPort;
import com.core.microservices.customers.domain.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerEventAdapter implements CustomerEventPort {

    private final CustomerKafkaProducer kafkaProducer;

    @Override
    public Mono<Void> publishCustomerCreated(Customer customer) {
        return Mono.fromRunnable(() -> {
            CustomerEvent event = CustomerEvent.builder()
                .eventType("CUSTOMER_CREATED")
                .customerId(customer.getId())
                .identification(customer.getIdentification())
                .name(customer.getName())
                .timestamp(System.currentTimeMillis())
                .build();
            
            kafkaProducer.publishEvent(event);
        });
    }

    @Override
    public Mono<Void> publishCustomerUpdated(Customer customer) {
        return Mono.fromRunnable(() -> {
            CustomerEvent event = CustomerEvent.builder()
                .eventType("CUSTOMER_UPDATED")
                .customerId(customer.getId())
                .identification(customer.getIdentification())
                .name(customer.getName())
                .timestamp(System.currentTimeMillis())
                .build();
            
            kafkaProducer.publishEvent(event);
        });
    }

    @Override
    public Mono<Void> publishCustomerDeleted(Long customerId) {
        return Mono.fromRunnable(() -> {
            CustomerEvent event = CustomerEvent.builder()
                .eventType("CUSTOMER_DELETED")
                .customerId(customerId)
                .timestamp(System.currentTimeMillis())
                .build();
            
            kafkaProducer.publishEvent(event);
        });
    }
}