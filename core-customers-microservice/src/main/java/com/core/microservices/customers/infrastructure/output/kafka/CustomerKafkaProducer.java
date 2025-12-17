package com.core.microservices.customers.infrastructure.output.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerKafkaProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;
  
  @Value("${app.kafka.customer-topic}")
  private String topic;

  public void publishEvent(CustomerEvent event) {
    log.info("[KAFKA] Publishing event - Type: {}, CustomerId: {}, Topic: {}", 
            event.getEventType(), event.getCustomerId(), topic);
    try {
      String key = Objects.toString(event.getCustomerId(), "unknown");
      kafkaTemplate.send(topic, key, event)
          .whenComplete((result, ex) -> {
            if (ex == null) {
              log.info("[KAFKA] Successfully published {} for customer ID: {} to topic: {}", 
                      event.getEventType(), event.getCustomerId(), topic);
            } else {
              log.error("[KAFKA] Failed to publish {} for customer ID: {} to topic: {}", 
                      event.getEventType(), event.getCustomerId(), topic, ex);
            }
          });
    } catch (Exception e) {
      log.error("[KAFKA] Exception while sending event to Kafka: ", e);
    }
  }
}
