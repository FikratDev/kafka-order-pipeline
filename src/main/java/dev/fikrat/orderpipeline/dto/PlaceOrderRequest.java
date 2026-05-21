package dev.fikrat.orderpipeline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record PlaceOrderRequest(
    @NotBlank String customerId,
    @NotEmpty List<String> items,
    @Positive BigDecimal totalAmount
) {}
