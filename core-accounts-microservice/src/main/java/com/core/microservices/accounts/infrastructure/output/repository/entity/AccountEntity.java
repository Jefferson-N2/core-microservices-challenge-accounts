package com.core.microservices.accounts.infrastructure.output.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "number")
    private String number;

    @Column(name = "type")
    private String type;

    @Column(name = "initial_balance")
    private BigDecimal initialBalance;

    @Column(name = "current_balance")
    private BigDecimal currentBalance;

    @Column(name = "status")
    private Boolean status;

}
