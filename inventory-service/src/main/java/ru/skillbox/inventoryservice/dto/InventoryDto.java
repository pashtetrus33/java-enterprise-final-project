package ru.skillbox.inventoryservice.dto;

import lombok.Data;

@Data
public class InventoryDto {

    private String description;
    private Integer quantity;
    private Integer costPerItem;
}