package com.core.microservices.accounts.integration;

import com.core.microservices.accounts.CoreAccountsMicroserviceApplication;
import com.core.microservices.accounts.application.input.port.AccountInputPort;
import com.core.microservices.accounts.application.output.port.AccountOutputPort;
import com.core.microservices.accounts.application.output.port.MovementOutputPort;
import com.core.microservices.accounts.domain.Account;
import com.core.microservices.accounts.domain.Movement;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(
        classes = CoreAccountsMicroserviceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DisplayName("Movement Integration Tests - F6 Requirement")
class MovementIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockitoBean
    private MovementOutputPort movementOutputPort;

    @MockitoBean
    private AccountOutputPort accountOutputPort;

    @Test
    @DisplayName("Should create movement and update account balance - End to End")
    void testCreateMovementAndUpdateAccountBalance() {
      
        when(kafkaTemplate.send(anyString(), any(Object.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        Account account = new Account();
        account.setId(1L);
        account.setNumber("478758");
        account.setCurrentBalance(BigDecimal.valueOf(1000));
        when(accountOutputPort.findByNumber(anyString())).thenReturn(Mono.just(account));

        Movement movement = new Movement();
        movement.setId(1L);
        movement.setAccountId(1L);
        movement.setType("Credito");
        movement.setValue(BigDecimal.valueOf(500));
        movement.setBalance(BigDecimal.valueOf(1500));
        movement.setDate(LocalDateTime.now());
        when(movementOutputPort.save(any(Movement.class))).thenReturn(Mono.just(movement));

        when(accountOutputPort.update(anyLong(), any(Account.class))).thenReturn(Mono.just(account));

        Map<String, Object> movementRequest = Map.of(
                "type", "Credito",
                "value", 500.00,
                "numberAccount", "478758",
                "description", "Integration test deposit"
        );

        webTestClient.post()
                .uri("/api/v1/movements")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(movementRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.balance").exists()
        ;
    }

    @Test
    @DisplayName("Should validate movement value is positive")
    void testValidateMovementValueIsPositive() throws Exception {

        Map<String, Object> movementRequest = Map.of(
                "type", "Credito",
                "value", -100.00, 
                "numberAccount", "478758",
                "description", "Invalid movement"
        );

        webTestClient.post()
                .uri("/api/v1/movements")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(movementRequest))
                .exchange()
                .expectStatus().is4xxClientError();
    }
}