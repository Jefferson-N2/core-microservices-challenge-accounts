package com.core.microservices.accounts.application.service;

import com.core.microservices.accounts.infrastructure.input.event.CustomerEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class CustomerCacheServiceTest {

    private CustomerCacheService customerCacheService;

    @BeforeEach
    void setUp() {
        customerCacheService = new CustomerCacheService();
    }

    @Test
    void testHandleCustomerCreated() {
        CustomerEvent event = new CustomerEvent();
        event.setEventType("CUSTOMER_CREATED");
        event.setCustomerId(1L);
        event.setIdentification("1234567890");
        event.setName("Jose Lema");

        StepVerifier.create(customerCacheService.handleCustomerCreated(event))
                .verifyComplete();

        StepVerifier.create(customerCacheService.getCustomerInfo(1L))
                .expectNextMatches(info -> 
                    info.getCustomerId().equals(1L) && 
                    "Jose Lema".equals(info.getName()) &&
                    "1234567890".equals(info.getIdentification()))
                .verifyComplete();
    }

    @Test
    void testHandleCustomerUpdated() {
        CustomerEvent createEvent = new CustomerEvent();
        createEvent.setEventType("CUSTOMER_CREATED");
        createEvent.setCustomerId(1L);
        createEvent.setIdentification("1234567890");
        createEvent.setName("Jose Lema");

        CustomerEvent updateEvent = new CustomerEvent();
        updateEvent.setEventType("CUSTOMER_UPDATED");
        updateEvent.setCustomerId(1L);
        updateEvent.setIdentification("1234567890");
        updateEvent.setName("Jose Lema Updated");

        StepVerifier.create(customerCacheService.handleCustomerCreated(createEvent)
                .then(customerCacheService.handleCustomerUpdated(updateEvent)))
                .verifyComplete();

        StepVerifier.create(customerCacheService.getCustomerInfo(1L))
                .expectNextMatches(info -> "Jose Lema Updated".equals(info.getName()))
                .verifyComplete();
    }

    @Test
    void testHandleCustomerDeleted() {
        CustomerEvent createEvent = new CustomerEvent();
        createEvent.setEventType("CUSTOMER_CREATED");
        createEvent.setCustomerId(1L);
        createEvent.setIdentification("1234567890");
        createEvent.setName("Jose Lema");

        CustomerEvent deleteEvent = new CustomerEvent();
        deleteEvent.setEventType("CUSTOMER_DELETED");
        deleteEvent.setCustomerId(1L);

        StepVerifier.create(customerCacheService.handleCustomerCreated(createEvent)
                .then(customerCacheService.handleCustomerDeleted(deleteEvent)))
                .verifyComplete();

        StepVerifier.create(customerCacheService.getCustomerInfo(1L))
                .verifyComplete();
    }
}