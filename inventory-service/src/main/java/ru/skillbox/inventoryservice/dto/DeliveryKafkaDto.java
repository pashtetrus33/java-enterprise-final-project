package ru.skillbox.inventoryservice.dto;

import lombok.Data;


@Data
public class DeliveryKafkaDto {

    private Long userId;
    private Long orderId;
    private Long invoiceId;
    private OrderDto orderDto;
    private String authHeaderValue;
}