package ru.skillbox.inventoryservice.service;


import ru.skillbox.inventoryservice.dto.StatusDto;

public interface RequestSendingService {

    void updateOrderStatusInOrderService(Long orderId, StatusDto statusDto, String authHeaderValue);
}