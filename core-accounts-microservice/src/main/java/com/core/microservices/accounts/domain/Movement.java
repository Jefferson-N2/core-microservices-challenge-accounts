package com.core.microservices.accounts.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movement {

    private Long id;
    private Long accountId;
    private LocalDateTime date;
    private String type;
    private BigDecimal value;
    private BigDecimal balance;
    private String description;

    private String accountNumber;
    private String customerName;


    public boolean isDebit() {
        return MovementConstants.DEBIT.equalsIgnoreCase(type);
    }

    public boolean isValidValue() {
        return Objects.nonNull(value) && value.compareTo(BigDecimal.ZERO) > 0;
    }
}