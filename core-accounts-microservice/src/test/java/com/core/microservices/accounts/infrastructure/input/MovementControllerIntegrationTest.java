package com.core.microservices.accounts.infrastructure.input;

import com.core.microservices.accounts.application.output.port.AccountOutputPort;
import com.core.microservices.accounts.application.output.port.MovementOutputPort;
import com.core.microservices.accounts.application.service.CustomerCacheService;
import com.core.microservices.accounts.domain.Account;
import com.core.microservices.accounts.domain.Movement;
import com.core.microservices.accounts.domain.exception.InsufficientBalanceException;
import com.core.microservices.accounts.infrastructure.input.rest.dto.MovementRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class MovementControllerMockedTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AccountOutputPort accountOutputPort;

    @MockitoBean
    private MovementOutputPort movementOutputPort;

    @MockitoBean
    private CustomerCacheService cacheService;

    @Test
    void testCreateMovementWithInsufficientBalanceMocked() {
        Account account = new Account();
        account.setId(1L);
        account.setNumber("478758");
        account.setCurrentBalance(BigDecimal.valueOf(100));

        when(accountOutputPort.findByNumber("478758")).thenReturn(Mono.just(account));

        when(movementOutputPort.save(any(Movement.class)))
                .thenReturn(Mono.error(new InsufficientBalanceException()));

        MovementRequest request = new MovementRequest()
                .type(MovementRequest.TypeEnum.DEBITO)
                .value(99999.0)
                .numberAccount("478758")
                .description("Insufficient balance test");

        webTestClient.post()
                .uri("/api/v1/movements")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Saldo no disponible");
    }
}
