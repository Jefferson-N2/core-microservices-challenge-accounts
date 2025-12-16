package com.core.microservices.customers.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.kafka")
public class KafkaProperties {
    
    private String customerTopic = "customer.events";
    private Producer producer = new Producer();
    
    @Data
    public static class Producer {
        private int retries = 3;
        private int batchSize = 16384;
        private int lingerMs = 1;
        private int bufferMemory = 33554432;
    }
}