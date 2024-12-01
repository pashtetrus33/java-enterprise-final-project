package ru.skillbox.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.orderservice.dto.enums.OrderStatus;
import ru.skillbox.orderservice.dto.enums.ServiceName;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusDto {

    private OrderStatus status;

    private ServiceName serviceName;

    private String comment;
}
