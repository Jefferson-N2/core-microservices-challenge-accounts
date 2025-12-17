package com.core.microservices.customers.integration;

import com.core.microservices.customers.CoreCustomersMicroserviceApplication;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(
        classes = CoreCustomersMicroserviceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DisplayName("Customer Integration Tests - F6 Requirement")
class CustomerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    void setup() {
        SendResult<String, Object> sendResult = new SendResult<>(null,
                new RecordMetadata(null, 0, 0, 0L, 0L, 0, 0));
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(sendResult);
        when(kafkaTemplate.send(anyString(), any(), any())).thenReturn(future);
    }

    @Test
    @DisplayName("Should create customer successfully - End to End")
    void testCreateCustomerSuccessfully() throws Exception {
        Map<String, Object> customerRequest = Map.of(
                "name", "Jose Lema",
                "gender", "MALE",
                "identification", "1234567890",
                "address", "Otavalo sn y principal",
                "phone", "098254785",
                "password", "1234",
                "status", true
        );

        webTestClient.post()
                .uri("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(customerRequest))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Jose Lema")
                .jsonPath("$.identification").isEqualTo("1234567890")
                .jsonPath("$.status").isEqualTo(true)
                .jsonPath("$.id").exists();
    }

    @Test
    @DisplayName("Should get all customers")
    void testGetAllCustomers() {

        webTestClient.get()
                .uri("/api/v1/customers")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray();
    }

    @Test
    @DisplayName("Should validate required fields")
    void testValidateRequiredFields() throws Exception {

        Map<String, Object> customerRequest = Map.of(
                "gender", "MALE",
                "identification", "1234567890"
        );


        webTestClient.post()
                .uri("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(customerRequest))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    @DisplayName("Should handle duplicate identification")
    void testHandleDuplicateIdentification() throws Exception {

        Map<String, Object> customerRequest = Map.of(
                "name", "Test Customer",
                "gender", "MALE",
                "identification", "9999999999",
                "address", "Test Address",
                "phone", "099999999",
                "password", "test123",
                "status", true
        );


        webTestClient.post()
                .uri("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(customerRequest))
                .exchange()
                .expectStatus().isCreated();


        webTestClient.post()
                .uri("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(customerRequest))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.message").exists();
    }
}
