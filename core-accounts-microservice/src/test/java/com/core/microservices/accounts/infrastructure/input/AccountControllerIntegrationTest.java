package com.core.microservices.accounts.infrastructure.input;

import com.core.microservices.accounts.application.output.port.AccountOutputPort;
import com.core.microservices.accounts.domain.Account;
import com.core.microservices.accounts.infrastructure.input.rest.dto.AccountRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class AccountControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AccountOutputPort accountOutputPort;

    @Test
    void testGetAllAccountsIntegration() {
        Account account1 = new Account();
        account1.setId(1L);
        account1.setType("Corriente");
        account1.setCurrentBalance(BigDecimal.valueOf(1000));

        Account account2 = new Account();
        account2.setId(2L);
        account2.setType("Ahorros");
        account2.setCurrentBalance(BigDecimal.valueOf(500));

        when(accountOutputPort.findAll()).thenReturn(Flux.fromIterable(List.of(account1, account2)));

        webTestClient.get()
                .uri("/api/v1/accounts")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].type").isEqualTo("Corriente")
                .jsonPath("$[1].type").isEqualTo("Ahorros");
    }

    @Test
    void testGetAccountByIdNotFoundIntegration() {
        when(accountOutputPort.findById(eq(99999L))).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/accounts/99999")
                .exchange()
                .expectStatus().isNotFound();
    }
}
