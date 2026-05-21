package dev.fikrat.orderpipeline.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    private String id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "item")
    private List<String> items;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private String status;

    @Column(name = "placed_at", nullable = false)
    private Instant placedAt;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    public Order() {}

    public Order(String id, String customerId, List<String> items,
                 BigDecimal totalAmount, Instant placedAt) {
        this.id = id;
        this.customerId = customerId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = "PROCESSED";
        this.placedAt = placedAt;
        this.processedAt = Instant.now();
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public List<String> getItems() { return items; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public Instant getPlacedAt() { return placedAt; }
    public Instant getProcessedAt() { return processedAt; }
}
