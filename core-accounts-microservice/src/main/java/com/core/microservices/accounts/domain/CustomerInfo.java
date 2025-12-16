package com.core.microservices.accounts.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInfo {
    private Long customerId;
    private String identification;
    private String name;
    
    public Long getId() {
        return customerId;
    }
}