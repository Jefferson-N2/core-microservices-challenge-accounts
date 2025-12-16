package com.core.microservices.accounts.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatement {
    
    private Long clientId;
    private String clientName;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<AccountSummary> accounts;
    
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountSummary {
        private String accountNumber;
        private String accountType;
        private Double currentBalance;
        private Double initialBalance;
        private List<Movement> movements;
    }
}