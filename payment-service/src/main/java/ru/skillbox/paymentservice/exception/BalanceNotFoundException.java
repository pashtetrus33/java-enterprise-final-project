package ru.skillbox.paymentservice.exception;

public class BalanceNotFoundException extends Exception {
    public BalanceNotFoundException(String message) {
        super(message);
    }
}