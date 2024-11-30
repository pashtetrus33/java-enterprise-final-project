package ru.skillbox.orderservice.dto;

import lombok.Data;

@Data
public class ErrorKafkaDto {

    private Long orderId;
    private StatusDto statusDto;
}