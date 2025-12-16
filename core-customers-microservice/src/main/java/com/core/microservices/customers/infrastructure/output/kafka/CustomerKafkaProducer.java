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
  
  @Value("${app.kafka.customer-topic:customer.events}")
  private String topic;

  public void publishEvent(CustomerEvent event) {
    log.info("Attempting to publish event: {} to topic: {}", event.getEventType(), topic);
    try {
      String key = Objects.toString(event.getCustomerId(), "unknown");
      kafkaTemplate.send(topic, key, event)
          .whenComplete((result, ex) -> {
            if (ex == null) {
              log.info("Successfully published customer event: {} to topic: {}", event.getEventType(), topic);
            } else {
              log.error("Failed to publish customer event to topic: {}", topic, ex);
            }
          });
    } catch (Exception e) {
      log.error("Exception while sending to Kafka: ", e);
    }
  }
}
