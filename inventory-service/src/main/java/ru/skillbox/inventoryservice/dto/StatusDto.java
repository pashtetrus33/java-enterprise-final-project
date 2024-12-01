package ru.skillbox.inventoryservice.dto;

import lombok.Data;
import ru.skillbox.inventoryservice.dto.enums.OrderStatus;
import ru.skillbox.inventoryservice.dto.enums.ServiceName;

@Data
public class StatusDto {

    private OrderStatus status;
    private ServiceName serviceName;
    private String comment;
}