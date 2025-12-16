package com.core.microservices.customers.infrastructure.output.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEvent {
    
    private String eventType;
    private Long customerId;
    private String identification;
    private String name;
    private Long timestamp;
    

}