package ru.skillbox.paymentservice.dto.enums;

public enum OrderStatus {
    REGISTERED,
    PAID,
    PAYMENT_FAILED,
    INVENTED,
    INVENTMENT_FAILED,
    DELIVERED,
    DELIVERY_FAILED,
    UNEXPECTED_FAILURE;
}
