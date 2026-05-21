package dev.fikrat.orderpipeline.messaging;

import dev.fikrat.orderpipeline.config.KafkaTopicConfig;
import dev.fikrat.orderpipeline.event.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderProducer.class);

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public OrderProducer(KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(OrderPlacedEvent event) {
        kafkaTemplate.send(KafkaTopicConfig.ORDERS_TOPIC, event.orderId(), event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish order event {}: {}", event.orderId(), ex.getMessage());
                } else {
                    log.info("Published order event {} to partition {}",
                        event.orderId(), result.getRecordMetadata().partition());
                }
            });
    }
}
