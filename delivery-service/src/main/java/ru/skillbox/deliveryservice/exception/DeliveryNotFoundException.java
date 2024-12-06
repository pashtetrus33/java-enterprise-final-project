package ru.skillbox.deliveryservice.exception;

public class DeliveryNotFoundException extends Exception {

    public DeliveryNotFoundException(String message) {
        super(message);
    }
}