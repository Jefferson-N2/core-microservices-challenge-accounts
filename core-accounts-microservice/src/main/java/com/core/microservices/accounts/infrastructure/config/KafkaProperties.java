package com.core.microservices.accounts.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.kafka")
public class KafkaProperties {
    
    private String customerTopic = "customer.events";
    private Consumer consumer = new Consumer();
    
    @Data
    public static class Consumer {
        private String groupId = "accounts-service";
        private String autoOffsetReset = "earliest";
        private boolean enableAutoCommit = true;
        private int autoCommitIntervalMs = 1000;
        private int sessionTimeoutMs = 30000;
    }
}