package ru.skillbox.paymentservice.dto.enums;

public enum OrderStatus {

    PAID,
    PAYMENT_FAILED,
    INVENTED,
    INVENTMENT_FAILED,
    DELIVERED,
    DELIVERY_FAILED,
    UNEXPECTED_FAILURE;
}
