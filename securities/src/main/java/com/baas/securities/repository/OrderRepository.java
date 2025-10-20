package com.baas.securities.repository;

import com.baas.securities.repository.entity.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Optional<Order> findById(String id);
    void save(Order order);
}
