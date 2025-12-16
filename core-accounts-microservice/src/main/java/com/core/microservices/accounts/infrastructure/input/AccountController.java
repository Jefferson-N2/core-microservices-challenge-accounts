package com.core.microservices.accounts.infrastructure.input;

import com.core.microservices.accounts.application.input.port.AccountInputPort;
import com.core.microservices.accounts.infrastructure.input.rest.api.AccountsApi;
import com.core.microservices.accounts.infrastructure.input.rest.dto.AccountRequest;
import com.core.microservices.accounts.infrastructure.input.rest.dto.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class AccountController implements AccountsApi {

    private final AccountInputPort accountInputPort;
    private final AccountDtoMapper accountDtoMapper;

    @Override
    public Mono<ResponseEntity<Flux<AccountResponse>>> getAllAccounts(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(
            accountInputPort.getAllAccounts().map(accountDtoMapper::toResponse)
        ));
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> createAccount(Mono<AccountRequest> accountRequest, ServerWebExchange exchange) {
        return accountRequest
            .map(accountDtoMapper::toDomain)
            .flatMap(accountInputPort::createAccount)
            .map(accountDtoMapper::toResponse)
            .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> getAccountById(Long id, ServerWebExchange exchange) {
        return accountInputPort.getAccountById(id)
            .map(accountDtoMapper::toResponse)
            .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> updateAccount(Long id, Mono<AccountRequest> accountRequest, ServerWebExchange exchange) {
        return accountRequest
            .map(accountDtoMapper::toDomain)
            .flatMap(account -> accountInputPort.updateAccount(id, account))
            .map(accountDtoMapper::toResponse)
            .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteAccount(Long id, ServerWebExchange exchange) {
        return accountInputPort.deleteAccount(id)
            .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}