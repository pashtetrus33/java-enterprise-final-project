package ru.skillbox.inventoryservice.dto;

import lombok.Data;

@Data
public class ErrorKafkaDto {

    private Long orderId;
    private StatusDto statusDto;
}