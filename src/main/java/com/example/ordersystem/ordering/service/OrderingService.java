package com.example.ordersystem.ordering.service;

import com.example.ordersystem.member.domain.Member;
import com.example.ordersystem.member.repository.MemberRepository;
import com.example.ordersystem.ordering.domain.OrderDetail;
import com.example.ordersystem.ordering.domain.OrderStatus;
import com.example.ordersystem.ordering.domain.Ordering;
import com.example.ordersystem.ordering.dto.OrderListResDto;
import com.example.ordersystem.ordering.dto.OrderSaveReqDto;
import com.example.ordersystem.ordering.repository.OrderingRepository;
import com.example.ordersystem.ordering.repository.OrderDetailRepository;
import com.example.ordersystem.product.domain.Product;
import com.example.ordersystem.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderingService {

    private final OrderingRepository orderingRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Autowired
    public OrderingService(OrderingRepository orderingRepository, MemberRepository memberRepository, ProductRepository productRepository, OrderDetailRepository orderDetailRepository) {
        this.orderingRepository  = orderingRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    /* 주분하기 */
    public Ordering orderCreate(List<OrderSaveReqDto> dtos) {
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
        //Member member = memberRepository.findById(dtos.getMemberId()).orElseThrow(()->new EntityNotFoundException("Member not found"));
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(memberEmail).orElseThrow(() -> new EntityNotFoundException("member not found"));
        Ordering ordering = Ordering.builder().member(member).build();
        for(OrderSaveReqDto orderDto : dtos){
            Product product = productRepository.findById(orderDto.getProductId()).orElse(null);
            int quantity = orderDto.getProductCount();
            if (product.getStockQuantity() < quantity){
                throw new EntityNotFoundException("재고부족");
            }
            product.updateStock(quantity); // 변경 감지(더티 체킹)로 인해 별도의 save 불필요함.
            OrderDetail orderDetail = OrderDetail.builder()
                    .product(product)
                    .quantity(quantity)
                    .ordering(ordering)
                    .build();
            ordering.getOrderDetails().add(orderDetail);
        }
        Ordering savedOrder = orderingRepository.save(ordering);
        return savedOrder;
    }

    /* 전체 리스트 */
    public List<OrderListResDto> orderList(){
        List<Ordering> orderings = orderingRepository.findAll();
        List<OrderListResDto> orderListResDtos = new ArrayList<>();
        for(Ordering ordering : orderings){
            orderListResDtos.add(ordering.fromEntityList());
        }
        return orderListResDtos;
    }

    /* 내 주문 보기 */
    public List<OrderListResDto> myOrders(){
        Member member =memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new EntityNotFoundException("Member not found"));
        List<Ordering> orderings = orderingRepository.findByMember(member);
        List<OrderListResDto> orderListResDtos = new ArrayList<>();
        for(Ordering ordering : orderings){
            orderListResDtos.add(ordering.fromEntityList());
        }
        return orderListResDtos;
    }

    /* 주문 취소 (admin 기준) */
    public Ordering orderCancel(Long id) {
        Ordering ordering = orderingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Ordering not found"));
        ordering.updateStatus(OrderStatus.CANCELLED);
        return ordering;
    }
}
