package com.core.microservices.accounts.domain;

import com.core.microservices.accounts.domain.exception.InsufficientBalanceException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    
    private Long id;
    private String number;
    private String type;
    private BigDecimal initialBalance;
    private BigDecimal currentBalance;
    private Boolean status;
    private Long customerId;
    private String identification;


    
    public static String generateAccountNumber() {
        return String.valueOf(Math.abs(UUID.randomUUID().hashCode()) % 1000000);
    }
    
    public BigDecimal credit(BigDecimal amount) {
        BigDecimal balance = currentBalance != null ? currentBalance : BigDecimal.ZERO;
        return balance.add(amount);
    }
    
    public BigDecimal debit(BigDecimal amount) {
        BigDecimal balance = currentBalance != null ? currentBalance : BigDecimal.ZERO;
        BigDecimal newBalance = balance.subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException();
        }
        return newBalance;
    }
    
}