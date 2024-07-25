package com.example.ordersystem.ordering.controller;

import com.example.ordersystem.common.dto.CommonResDto;
import com.example.ordersystem.ordering.domain.Ordering;
import com.example.ordersystem.ordering.dto.OrderSaveReqDto;
import com.example.ordersystem.ordering.service.OrderingService;
import com.example.ordersystem.product.domain.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    private final OrderingService orderingService;

    public OrderController(OrderingService orderingService) {
        this.orderingService = orderingService;
    }

    @PostMapping("/order/create") // 생성
    public ResponseEntity<?> orderingCreate(OrderSaveReqDto dto) {
        Ordering ordering = orderingService.OrderCreate(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "success created", ordering.getId());
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }
}
