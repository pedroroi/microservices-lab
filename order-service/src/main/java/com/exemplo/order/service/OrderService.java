package com.exemplo.order.service;

import com.exemplo.order.dto.OrderRequest;
import com.exemplo.order.dto.OrderResponse;
import com.exemplo.order.model.Order;
import com.exemplo.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderResponse placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setPrice(orderRequest.price());
        order.setSkuCode(orderRequest.skuCode());
        order.setQuantity(orderRequest.quantity());
        Order saved = orderRepository.save(order);
        log.info("Pedido criado com sucesso!: id={}", saved.getId());

        return new OrderResponse(
                saved.getId(),
                saved.getOrderNumber(),
                saved.getSkuCode(),
                saved.getPrice(),
                saved.getQuantity()
        );
    }
}
