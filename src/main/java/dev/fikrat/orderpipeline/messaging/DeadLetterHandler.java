package dev.fikrat.orderpipeline.messaging;

import dev.fikrat.orderpipeline.config.KafkaTopicConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumes messages that failed all processing retries in the main topic.
 * Spring Kafka routes them here automatically when the error handler gives up.
 * Real systems would persist these for manual review or alerting.
 */
@Component
public class DeadLetterHandler {

    private static final Logger log = LoggerFactory.getLogger(DeadLetterHandler.class);

    @KafkaListener(topics = KafkaTopicConfig.ORDERS_DLT, groupId = "order-dlt-processor")
    public void handle(ConsumerRecord<String, Object> record) {
        log.error("Dead-letter received — partition={} offset={} key={} value={}",
            record.partition(), record.offset(), record.key(), record.value());
    }
}
