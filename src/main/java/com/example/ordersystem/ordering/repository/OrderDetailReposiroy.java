package com.example.ordersystem.ordering.repository;

import com.example.ordersystem.ordering.domain.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailReposiroy extends JpaRepository<OrderDetail, Integer> {
}
