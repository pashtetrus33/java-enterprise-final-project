package ru.skillbox.deliveryservice.dto;

import lombok.Data;
import ru.skillbox.deliveryservice.dto.enums.OrderStatus;
import ru.skillbox.deliveryservice.dto.enums.ServiceName;

@Data
public class StatusDto {

    private OrderStatus status;
    private ServiceName serviceName;
    private String comment;
}