package ru.skillbox.paymentservice.dto;

import lombok.Data;


@Data
public class InventoryKafkaDto {

    private Long userId;
    private Long orderId;
    private OrderDto orderDto;
    private String authHeaderValue;

}