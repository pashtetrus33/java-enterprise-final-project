package ru.skillbox.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

    private String description;

    private String departureAddress;

    private String destinationAddress;

    private Long cost;

    private Integer quantity;
}