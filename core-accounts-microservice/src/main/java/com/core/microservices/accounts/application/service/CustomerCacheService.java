package com.core.microservices.accounts.application.service;

import com.core.microservices.accounts.domain.CustomerInfo;
import com.core.microservices.accounts.infrastructure.input.event.CustomerEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Slf4j
@Service
public class CustomerCacheService {

    private final Map<Long, CustomerInfo> customerCache = new ConcurrentHashMap<>();

    @Value("${app.external-services.customers-service.url}")
    private String customersServiceUrl;

    @Value("${app.external-services.customers-service.timeout:3000}")
    private int timeout;

    public Mono<Void> handleCustomerCreated(CustomerEvent event) {
        return Mono.fromRunnable(() -> {
            CustomerInfo customerInfo = CustomerInfo.builder()
                    .customerId(event.getCustomerId())
                    .name(event.getName())
                    .identification(event.getIdentification())
                    .build();

            customerCache.put(event.getCustomerId(), customerInfo);
            log.info("[CACHE] Customer cached - ID: {}, Name: {}, Identification: {}, Cache size: {}",
                    event.getCustomerId(), event.getName(), event.getIdentification(), customerCache.size());
        });
    }

    public Mono<Void> handleCustomerUpdated(CustomerEvent event) {
        return Mono.fromRunnable(() -> {
            CustomerInfo existing = customerCache.get(event.getCustomerId());
            if (existing != null) {
                existing.setName(event.getName());
                existing.setIdentification(event.getIdentification());
                log.info("[CACHE] Customer updated - ID: {}, Name: {}, Cache size: {}",
                        event.getCustomerId(), event.getName(), customerCache.size());
            } else {
                log.warn("[CACHE] Customer not found in cache for update - ID: {}", event.getCustomerId());
            }
        });
    }

    public Mono<Void> handleCustomerDeleted(CustomerEvent event) {
        return Mono.fromRunnable(() -> {
            CustomerInfo removed = customerCache.remove(event.getCustomerId());
            if (removed != null) {
                log.info("[CACHE] Customer removed - ID: {}, Cache size: {}",
                        event.getCustomerId(), customerCache.size());
            } else {
                log.warn("[CACHE] Customer not found in cache for deletion - ID: {}", event.getCustomerId());
            }
        });
    }

    public Mono<CustomerInfo> getCustomerInfo(Long customerId) {
        return Mono.justOrEmpty(customerCache.get(customerId))
                .switchIfEmpty(getCustomerByIdFromService(customerId));
    }

    public Mono<CustomerInfo> getCustomerById(Long customerId) {
        return getCustomerInfo(customerId);
    }

    public Mono<CustomerInfo> getCustomerByIdentification(String identification) {
        return Mono.justOrEmpty(
                customerCache.values().stream()
                        .filter(customer -> identification.equals(customer.getIdentification()))
                        .findFirst()
                        .orElse(null)
        ).switchIfEmpty(getCustomerFromService(identification))
         .doOnNext(customer -> {
             if (customer == null) {
                 log.warn("[CACHE] Customer not found in cache for identification: {}. Cache size: {}",
                         identification, customerCache.size());
             }
         });
    }

    private Mono<CustomerInfo> getCustomerFromService(String identification) {
        return WebClient.builder()
                .build()
                .get()
                .uri(customersServiceUrl + "/api/v1/customers/identification/{identification}", identification)
                .retrieve()
                .bodyToMono(CustomerResponse.class)
                .map(this::toCustomerInfo)
                .doOnNext(customer -> {
                    log.info("[CACHE] Retrieved customer from service: {}", customer.getName());
                    customerCache.put(customer.getCustomerId(), customer);
                })
                .onErrorResume(ex -> {
                    log.error("[CACHE] Failed to retrieve customer from service: {}", ex.getMessage());
                    return Mono.empty();
                });
    }

    private Mono<CustomerInfo> getCustomerByIdFromService(Long customerId) {
        return WebClient.builder()
                .build()
                .get()
                .uri(customersServiceUrl + "/api/v1/customers/{id}", customerId)
                .retrieve()
                .bodyToMono(CustomerResponse.class)
                .map(this::toCustomerInfo)
                .doOnNext(customer -> {
                    log.info("[CACHE] Retrieved customer by ID from service: {}", customer.getName());
                    customerCache.put(customer.getCustomerId(), customer);
                })
                .onErrorResume(ex -> {
                    log.error("[CACHE] Failed to retrieve customer by ID from service: {}", ex.getMessage());
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
