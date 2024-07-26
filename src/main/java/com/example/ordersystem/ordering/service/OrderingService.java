package com.example.ordersystem.ordering.service;

import com.example.ordersystem.member.domain.Member;
import com.example.ordersystem.member.repository.MemberRepository;
import com.example.ordersystem.ordering.domain.OrderDetail;
import com.example.ordersystem.ordering.domain.Ordering;
import com.example.ordersystem.ordering.dto.OrderListResDto;
import com.example.ordersystem.ordering.dto.OrderSaveReqDto;
import com.example.ordersystem.ordering.repository.OrderingRepository;
import com.example.ordersystem.product.domain.Product;
import com.example.ordersystem.ordering.repository.OrderDetailReposiroy;
import com.example.ordersystem.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderingService {

    private final OrderingRepository orderingRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderDetailReposiroy orderDetailReposiroy;

    public OrderingService(OrderingRepository orderingRepository, MemberRepository memberRepository, ProductRepository productRepository, OrderDetailReposiroy orderDetailReposiroy) {
        this.orderingRepository  = orderingRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
        this.orderDetailReposiroy = orderDetailReposiroy;
    }

    public Ordering OrderCreate(OrderSaveReqDto dto) {
       // 방법1. 쉬운방식
       //Ordering생성 : member_id, status
//        Member member = memberRepository.findById(dto.getMemberId()).orElseThrow(()->new EntityNotFoundException("없음"));
//        Ordering ordering = orderingRepository.save(dto.toEntity(member));
//
//       //OrderDetail생성 : order_id, product_id, quantity
//        for(OrderSaveReqDto.OrderDetailDto orderDto : dto.getOrderDetailDtoList()){
//            Product product = productRepository.findById(orderDto.getProductId()).orElse(null);
//            int quantity = orderDto.getProductCount();
//            OrderDetail orderDetail =  OrderDetail.builder()
//                    .product(product)
//                    .quantity(quantity)
//                    .ordering(ordering)
//                    .build();
//            orderDetailReposiroy.save(orderDetail);
//        }
//        return ordering;


        // 방법 2. JPA 에 최적화 된 방식
        Member member = memberRepository.findById(dto.getMemberId()).orElseThrow(()->new EntityNotFoundException("Member not found"));

        Ordering ordering = Ordering.builder()
                .member(member)
                .build();
        for(OrderSaveReqDto.OrderDetailDto orderDto : dto.getOrderDetailDtoList()){
            Product product = productRepository.findById(orderDto.getProductId()).orElse(null);
            int quantity = orderDto.getProductCount();
            if (product.getStockQuantity() < quantity){
                throw new EntityNotFoundException("재고부족");
            } product.updateStock(quantity); //변경감지로 별도 save문 필요
            OrderDetail orderDetail =  OrderDetail.builder()
                    .product(product)
                    .quantity(quantity)
                    .ordering(ordering)
                    .build();
            ordering.getOrderDetails().add(orderDetail);
        }
        Ordering savedOrder = orderingRepository.save(ordering);
        return savedOrder;
    }
    public Ordering OrderUpdate(OrderSaveReqDto dto) {
        return null;
    }

    public List<OrderListResDto> orderList(){
        List<Ordering> orderings = orderingRepository.findAll();
        List<OrderListResDto> orderListResDtos = new ArrayList<>();
        for(Ordering ordering : orderings){
            orderListResDtos.add(ordering.fromEntityList());
        }
        return orderListResDtos;
    }




}
