package com.baas.securities.repository.impl;

import com.baas.securities.repository.OrderRepository;
import com.baas.securities.repository.entity.Order;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Qualifier("OrderRepository")
public class OrderRepositoryImpl implements OrderRepository {
    private final Map<String, Order> store = new ConcurrentHashMap<>();

    @Override
    public Optional<Order> findById(String id) {
        Order order = store.get(id);
        return Optional.of(order);
    }

    @Override
    public void save(Order order) {
        store.put(order.getId(),order);
    }
}
