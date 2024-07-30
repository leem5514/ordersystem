package com.example.ordersystem.ordering.service;

import com.example.ordersystem.common.configs.RabbitMqConfig;
import com.example.ordersystem.ordering.dto.StockDecreaseEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StockDecreaseEventHandler {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publish(StockDecreaseEvent event){
        rabbitTemplate.convertAndSend(RabbitMqConfig.STOCK_DECREASE_QUEUE, event);
    }

    @Transactional
    @RabbitListener(queues = RabbitMqConfig.STOCK_DECREASE_QUEUE) // 선언된 큐만 바라보고 있다가 메세지를 받아서 redis 처리
    public void listen(){

    }
}