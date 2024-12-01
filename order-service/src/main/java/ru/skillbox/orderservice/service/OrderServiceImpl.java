package ru.skillbox.orderservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.orderservice.controller.OrderNotFoundException;
import ru.skillbox.orderservice.model.*;
import ru.skillbox.orderservice.dto.OrderDto;
import ru.skillbox.orderservice.dto.PaymentKafkaDto;
import ru.skillbox.orderservice.dto.StatusDto;
import ru.skillbox.orderservice.dto.enums.OrderStatus;
import ru.skillbox.orderservice.dto.enums.ServiceName;
import ru.skillbox.orderservice.repository.OrderRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final KafkaService kafkaService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, KafkaService kafkaService) {
        this.orderRepository = orderRepository;
        this.kafkaService = kafkaService;
    }

    @Transactional
    @Override
    public Optional<Order> addOrder(OrderDto orderDto, HttpServletRequest request) {

        Long userId = Long.valueOf(request.getHeader("id"));
        String authHeaderValue = request.getHeader("Authorization");

        Order newOrder = new Order(
                orderDto.getDepartureAddress(),
                orderDto.getDestinationAddress(),
                orderDto.getDescription(),
                orderDto.getCost(),
                userId,
                orderDto.getQuantity(),
                OrderStatus.REGISTERED
        );
        newOrder.addStatusHistory(newOrder.getStatus(), ServiceName.ORDER_SERVICE, "Order created");
        newOrder.setCreationTime(LocalDateTime.now());
        newOrder.setModifiedTime(LocalDateTime.now());
        Order savedOrder = orderRepository.save(newOrder);

        PaymentKafkaDto paymentKafkaDto = createPaymentKafkaDto(userId, orderDto, savedOrder.getId(), authHeaderValue);
        kafkaService.produce(paymentKafkaDto);

        return Optional.of(savedOrder);
    }

    @Transactional
    @Override
    public void updateOrderStatus(Long id, StatusDto statusDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        if (order.getStatus() == statusDto.getStatus()) {
            log.info("Request with same status {} for order {} from service {}", statusDto.getStatus(), id, statusDto.getServiceName());
            return;
        }
        order.setStatus(statusDto.getStatus());
        order.addStatusHistory(statusDto.getStatus(), statusDto.getServiceName(), statusDto.getComment());
        orderRepository.save(order);
    }

    private PaymentKafkaDto createPaymentKafkaDto(Long userId, OrderDto orderDto, Long orderId, String authHeaderValue) {

        PaymentKafkaDto paymentKafkaDto = new PaymentKafkaDto();
        paymentKafkaDto.setUserId(userId);
        paymentKafkaDto.setOrderId(orderId);
        paymentKafkaDto.setOrderDto(orderDto);
        paymentKafkaDto.setAuthHeaderValue(authHeaderValue);

        return paymentKafkaDto;
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}