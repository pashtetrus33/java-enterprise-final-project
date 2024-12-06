package ru.skillbox.deliveryservice.dto;

import lombok.Data;

@Data
public class DeliveryKafkaDto {

    private Long userId;
    private Long invoiceId;
    private Long orderId;
    private OrderDto orderDto;
    private String authHeaderValue;
}