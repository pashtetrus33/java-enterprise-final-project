package ru.skillbox.paymentservice.service;

import ru.skillbox.paymentservice.dto.StatusDto;

public interface RequestSendingService {

    void updateOrderStatusInOrderService(Long orderId, StatusDto statusDto, String authHeaderValue);
}