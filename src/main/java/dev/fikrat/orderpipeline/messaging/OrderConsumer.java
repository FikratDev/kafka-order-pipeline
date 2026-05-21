package dev.fikrat.orderpipeline.messaging;

import dev.fikrat.orderpipeline.config.KafkaTopicConfig;
import dev.fikrat.orderpipeline.event.OrderPlacedEvent;
import dev.fikrat.orderpipeline.model.Order;
import dev.fikrat.orderpipeline.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderConsumer.class);

    private final OrderRepository repository;

    public OrderConsumer(OrderRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = KafkaTopicConfig.ORDERS_TOPIC, groupId = "order-processor")
    public void handle(OrderPlacedEvent event) {
        if (repository.existsById(event.orderId())) {
            log.debug("Skipping duplicate event for order {}", event.orderId());
            return;
        }
        Order order = new Order(
            event.orderId(),
            event.customerId(),
            event.items(),
            event.totalAmount(),
            event.placedAt()
        );
        repository.save(order);
        log.info("Processed order {} for customer {}", event.orderId(), event.customerId());
    }
}
