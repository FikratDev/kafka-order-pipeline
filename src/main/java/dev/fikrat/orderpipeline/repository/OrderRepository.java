package dev.fikrat.orderpipeline.repository;

import dev.fikrat.orderpipeline.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {}
