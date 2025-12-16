package com.core.microservices.accounts.infrastructure.input.event;

import com.core.microservices.accounts.application.service.CustomerCacheService;
import com.core.microservices.accounts.infrastructure.config.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
// @Component
@RequiredArgsConstructor
public class CustomerEventListener {

    private final KafkaProperties kafkaProperties;
    private final CustomerCacheService customerCacheService;

    // @KafkaListener(topics = "#{kafkaProperties.customerTopic}", groupId = "#{kafkaProperties.consumer.groupId}")
    public void handleCustomerEvent(CustomerEvent event) {
        log.info("Received customer event: {}", event);
        
        switch (event.getEventType()) {
            case "CUSTOMER_CREATED":
                customerCacheService.handleCustomerCreated(event).subscribe();
                break;
            case "CUSTOMER_UPDATED":
                customerCacheService.handleCustomerUpdated(event).subscribe();
                break;
            case "CUSTOMER_DELETED":
                customerCacheService.handleCustomerDeleted(event).subscribe();
                break;
            default:
                log.warn("Unknown event type: {}", event.getEventType());
        }
    }
}