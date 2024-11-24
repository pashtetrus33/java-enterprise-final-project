package ru.skillbox.orderservice.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import ru.skillbox.orderservice.controller.ErrorException;
import ru.skillbox.orderservice.controller.OrderNotFoundException;

@AllArgsConstructor
@Data
public class OrderKafkaDto {

    private Long id;

    private String status;

    private String creationTime;

    private String modifiedTime;

    public static OrderKafkaDto toKafkaDto(Order order) {

        if (order.getCreationTime() == null) {
            throw new ErrorException("Creation time is not set");
        }


        return new OrderKafkaDto(
                order.getId(),
                order.getStatus().toString(),
                order.getCreationTime().toString(),
                order.getModifiedTime().toString()
        );

    }
}
