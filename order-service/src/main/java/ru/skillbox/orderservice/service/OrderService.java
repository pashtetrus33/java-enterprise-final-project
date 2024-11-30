package ru.skillbox.orderservice.service;


import ru.skillbox.orderservice.controller.OrderNotFoundException;
import ru.skillbox.orderservice.model.Order;
import ru.skillbox.orderservice.dto.OrderDto;
import ru.skillbox.orderservice.dto.StatusDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    Optional<Order> addOrder(OrderDto orderDto, HttpServletRequest request);

    void updateOrderStatus(Long id, StatusDto statusDto) throws OrderNotFoundException;

    List<Order> findAll();

    Order findById(Long orderId);
}