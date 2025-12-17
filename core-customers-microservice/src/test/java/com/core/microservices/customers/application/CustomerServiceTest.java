package com.core.microservices.customers.application;

import com.core.microservices.customers.application.output.port.CustomerEventPort;
import com.core.microservices.customers.application.output.port.CustomerOutputPort;
import com.core.microservices.customers.application.service.CustomerService;
import com.core.microservices.customers.domain.Customer;
import com.core.microservices.customers.infrastructure.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerOutputPort customerOutputPort;

    @Mock
    private CustomerEventPort customerEventPort;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
            .customerId(1L)
            .name("Jose Lema")
            .gender("MALE")
            .identification("1234567890")
            .address("Otavalo sn y principal")
            .phone("098254785")
            .password("1234")
            .status(true)
            .build();
    }

    @Test
    void testCreateCustomerSuccessfully() {
        when(customerOutputPort.existsByIdentification(anyString())).thenReturn(Mono.just(false));
        when(customerOutputPort.save(any(Customer.class))).thenReturn(Mono.just(testCustomer));
        when(customerEventPort.publishCustomerCreated(any(Customer.class))).thenReturn(Mono.empty());

        StepVerifier.create(customerService.createCustomer(testCustomer))
            .expectNextMatches(customer -> {
                assert customer.getName().equals("Jose Lema") : 
                    "Customer name should be 'Jose Lema' but was " + customer.getName();
                assert customer.getIdentification().equals("1234567890") : 
                    "Customer identification should be '1234567890' but was " + customer.getIdentification();
                return true;
            })
            .verifyComplete();
    }

    @Test
    void testCreateCustomerWithDuplicateIdentification() {
        when(customerOutputPort.existsByIdentification(anyString())).thenReturn(Mono.just(true));

        StepVerifier.create(customerService.createCustomer(testCustomer))
            .expectErrorMatches(throwable -> {
                assert throwable instanceof RuntimeException : 
                    "Expected RuntimeException but got " + throwable.getClass().getSimpleName();
                assert throwable.getMessage().contains("Customer already exists") : 
                    "Expected message about duplicate customer but got '" + throwable.getMessage() + "'";
                return true;
            })
            .verify();
    }

    @Test
    void testGetCustomerByIdNotFound() {
        when(customerOutputPort.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(customerService.getCustomerById(99999L))
            .expectErrorMatches(throwable -> {
                assert throwable instanceof NotFoundException : 
                    "Expected NotFoundException but got " + throwable.getClass().getSimpleName();
                return true;
            })
            .verify();
    }

    @Test
    void testGetCustomerByIdentificationSuccessfully() {
        when(customerOutputPort.findByIdentification(anyString())).thenReturn(Mono.just(testCustomer));

        StepVerifier.create(customerService.getCustomerByIdentification("1234567890"))
            .expectNextMatches(customer -> {
                assert customer.getIdentification().equals("1234567890") : 
                    "Customer identification should be '1234567890' but was " + customer.getIdentification();
                return true;
            })
            .verifyComplete();
    }

    @Test
    void testGetAllCustomers() {
        when(customerOutputPort.findAll()).thenReturn(Flux.just(testCustomer));

        StepVerifier.create(customerService.getAllCustomers())
            .expectNextCount(1)
            .verifyComplete();
    }

    @Test
    void testUpdateCustomerSuccessfully() {
        when(customerOutputPort.update(anyLong(), any(Customer.class))).thenReturn(Mono.just(testCustomer));
        when(customerEventPort.publishCustomerUpdated(any(Customer.class))).thenReturn(Mono.empty());

        StepVerifier.create(customerService.updateCustomer(1L, testCustomer))
            .expectNextMatches(customer -> {
                assert customer.getName().equals("Jose Lema") : 
                    "Updated customer name should be 'Jose Lema' but was " + customer.getName();
                return true;
            })
            .verifyComplete();
    }

    @Test
    void testDeleteCustomerSuccessfully() {
        when(customerOutputPort.deleteById(anyLong())).thenReturn(Mono.empty());
        when(customerEventPort.publishCustomerDeleted(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(customerService.deleteCustomer(1L))
            .verifyComplete();
    }
}
