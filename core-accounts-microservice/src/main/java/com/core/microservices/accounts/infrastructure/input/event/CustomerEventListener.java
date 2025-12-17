package com.core.microservices.accounts.infrastructure.input.event;

import com.core.microservices.accounts.application.service.CustomerCacheService;
import com.core.microservices.accounts.infrastructure.config.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerEventListener {

    private final KafkaProperties kafkaProperties;
    private final CustomerCacheService customerCacheService;

    @KafkaListener(topics = "${app.kafka.customer-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleCustomerEvent(CustomerEvent event) {
        log.info("[KAFKA] Received customer event - Type: {}, CustomerId: {}, Name: {}", 
                event.getEventType(), event.getCustomerId(), event.getName());
        
        switch (event.getEventType()) {
            case "CUSTOMER_CREATED":
                customerCacheService.handleCustomerCreated(event)
                    .doOnSuccess(v -> log.info("[KAFKA] Successfully processed CUSTOMER_CREATED for ID: {}", event.getCustomerId()))
                    .doOnError(e -> log.error("[KAFKA] Error processing CUSTOMER_CREATED: ", e))
                    .subscribe();
                break;
            case "CUSTOMER_UPDATED":
                customerCacheService.handleCustomerUpdated(event)
                    .doOnSuccess(v -> log.info("[KAFKA] Successfully processed CUSTOMER_UPDATED for ID: {}", event.getCustomerId()))
                    .doOnError(e -> log.error("[KAFKA] Error processing CUSTOMER_UPDATED: ", e))
                    .subscribe();
                break;
            case "CUSTOMER_DELETED":
                customerCacheService.handleCustomerDeleted(event)
                    .doOnSuccess(v -> log.info("[KAFKA] Successfully processed CUSTOMER_DELETED for ID: {}", event.getCustomerId()))
                    .doOnError(e -> log.error("[KAFKA] Error processing CUSTOMER_DELETED: ", e))
                    .subscribe();
                break;
            default:
                log.warn("[KAFKA] Unknown event type: {}", event.getEventType());
        }
    }
}