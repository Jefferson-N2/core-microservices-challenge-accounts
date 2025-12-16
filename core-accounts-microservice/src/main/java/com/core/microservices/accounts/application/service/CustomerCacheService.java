package com.core.microservices.accounts.application.service;

import com.core.microservices.accounts.domain.CustomerInfo;
import com.core.microservices.accounts.infrastructure.input.event.CustomerEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Slf4j
@Service
public class CustomerCacheService {

    private final Map<Long, CustomerInfo> customerCache = new ConcurrentHashMap<>();

    public Mono<Void> handleCustomerCreated(CustomerEvent event) {
        return Mono.fromRunnable(() -> {
            CustomerInfo customerInfo = CustomerInfo.builder()
                    .customerId(event.getCustomerId())
                    .name(event.getName())
                    .identification(event.getIdentification())
                    .build();
            
            customerCache.put(event.getCustomerId(), customerInfo);
            log.info("Customer cached: {} - {}", event.getCustomerId(), event.getName());
        });
    }

    public Mono<Void> handleCustomerUpdated(CustomerEvent event) {
        return Mono.fromRunnable(() -> {
            CustomerInfo existing = customerCache.get(event.getCustomerId());
            if (existing != null) {
                existing.setName(event.getName());
                existing.setIdentification(event.getIdentification());
                log.info("Customer updated in cache: {} - {}", event.getCustomerId(), event.getName());
            }
        });
    }

    public Mono<Void> handleCustomerDeleted(CustomerEvent event) {
        return Mono.fromRunnable(() -> {
            customerCache.remove(event.getCustomerId());
            log.info("Customer removed from cache: {}", event.getCustomerId());
        });
    }

    public Mono<CustomerInfo> getCustomerInfo(Long customerId) {
        return Mono.fromCallable(() -> customerCache.get(customerId));
    }

    public Mono<CustomerInfo> getCustomerByIdentification(String identification) {
        return Mono.fromCallable(() -> 
            customerCache.values().stream()
                .filter(customer -> identification.equals(customer.getIdentification()))
                .findFirst()
                .orElse(null)
        ).switchIfEmpty(
            // Fallback: call customers service directly
            getCustomerFromService(identification)
        ).doOnNext(customer -> {
            if (customer == null) {
                log.warn("Customer not found in cache for identification: {}. Cache size: {}", identification, customerCache.size());
            }
        });
    }
    
    @org.springframework.beans.factory.annotation.Value("${app.external-services.customers-service.url}")
    private String customersServiceUrl;
    
    @org.springframework.beans.factory.annotation.Value("${app.external-services.customers-service.timeout}")
    private int timeout;
    
    private Mono<CustomerInfo> getCustomerFromService(String identification) {
        return WebClient.builder()
                .build()
                .get()
                .uri(customersServiceUrl + "/api/v1/customers/identification/{identification}", identification)
                .retrieve()
                .bodyToMono(CustomerResponse.class)
                .map(this::toCustomerInfo)
                .doOnNext(customer -> {
                    log.info("Retrieved customer from service: {}", customer.getName());
                    // Cache the customer for future use
                    customerCache.put(customer.getCustomerId(), customer);
                })
                .onErrorResume(ex -> {
                    log.error("Failed to retrieve customer from service: {}", ex.getMessage());
                    return Mono.empty();
                });
    }
    
    private CustomerInfo toCustomerInfo(CustomerResponse response) {
        return CustomerInfo.builder()
                .customerId(response.getId())
                .identification(response.getIdentification())
                .name(response.getName())
                .build();
    }
    
    private static class CustomerResponse {
        private Long id;
        private String identification;
        private String name;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getIdentification() { return identification; }
        public void setIdentification(String identification) { this.identification = identification; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }


}