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
public class Person {
    
    private Long id;
    private String name;
    private String gender;
    private String identification;
    private String address;
    private String phone;
    

}