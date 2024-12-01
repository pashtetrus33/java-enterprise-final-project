package ru.skillbox.inventoryservice.exception;

public class InventoryNotFoundException extends Exception {

    public InventoryNotFoundException(String message) {
        super(message);
    }
}