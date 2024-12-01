package ru.skillbox.inventoryservice.dto;

import lombok.Data;


@Data
public class InventoryKafkaDto {

    private Long userId;
    private Long orderId;
    private OrderDto orderDto;
    private String authHeaderValue;
}