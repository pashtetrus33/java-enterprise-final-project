package ru.skillbox.deliveryservice.service;

import ru.skillbox.deliveryservice.dto.StatusDto;

public interface RequestSendingService {

    void updateOrderStatusInOrderService(Long orderId, StatusDto statusDto, String authHeaderValue);
}