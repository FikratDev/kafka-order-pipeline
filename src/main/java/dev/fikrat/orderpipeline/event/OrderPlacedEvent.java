package dev.fikrat.orderpipeline.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderPlacedEvent(
    String orderId,
    String customerId,
    List<String> items,
    BigDecimal totalAmount,
    Instant placedAt
) {}
