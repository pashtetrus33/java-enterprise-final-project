package ru.skillbox.inventoryservice.dto;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class QuantityDto {

    @Min(1)
    private Integer quantity;
}