package ru.skillbox.paymentservice.exception;

public class BalanceExistsException extends Exception {
    public BalanceExistsException(String message) {
        super(message);
    }
}