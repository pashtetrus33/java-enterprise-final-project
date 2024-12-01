package ru.skillbox.inventoryservice.exception;

public class NotEnoughInventoryException extends RuntimeException {

    public NotEnoughInventoryException(String message) {
        super(message);
    }
}