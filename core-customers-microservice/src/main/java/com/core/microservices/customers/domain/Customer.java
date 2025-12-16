package com.core.microservices.customers.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends Person {
    
    private Long customerId;
    private String password;
    private Boolean status;
    
    public boolean isActive() {
        return Boolean.TRUE.equals(status);
    }
}