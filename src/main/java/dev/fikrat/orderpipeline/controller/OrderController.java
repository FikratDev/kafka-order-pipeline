package dev.fikrat.orderpipeline.controller;

import dev.fikrat.orderpipeline.dto.PlaceOrderRequest;
import dev.fikrat.orderpipeline.event.OrderPlacedEvent;
import dev.fikrat.orderpipeline.messaging.OrderProducer;
import dev.fikrat.orderpipeline.model.Order;
import dev.fikrat.orderpipeline.repository.OrderRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders")
public class OrderController {

    private final OrderProducer producer;
    private final OrderRepository repository;

    public OrderController(OrderProducer producer, OrderRepository repository) {
        this.producer = producer;
        this.repository = repository;
    }

    @PostMapping
    @Operation(summary = "Place an order — publishes an event to Kafka")
    public ResponseEntity<Map<String, String>> place(@Valid @RequestBody PlaceOrderRequest request) {
        String orderId = UUID.randomUUID().toString();
        OrderPlacedEvent event = new OrderPlacedEvent(
            orderId,
            request.customerId(),
            request.items(),
            request.totalAmount(),
            Instant.now()
        );
        producer.publish(event);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .body(Map.of("orderId", orderId, "status", "ACCEPTED"));
    }

    @GetMapping
    @Operation(summary = "List all processed orders")
    public List<Order> list() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a processed order by ID")
    public ResponseEntity<Order> get(@PathVariable String id) {
        return repository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
