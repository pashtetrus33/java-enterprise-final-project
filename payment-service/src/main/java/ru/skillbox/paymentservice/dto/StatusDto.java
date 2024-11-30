package ru.skillbox.paymentservice.dto;

import lombok.Data;
import ru.skillbox.paymentservice.dto.enums.OrderStatus;
import ru.skillbox.paymentservice.dto.enums.ServiceName;

@Data
public class StatusDto {

    private OrderStatus status;
    private ServiceName serviceName;
    private String comment;
}