package ru.skillbox.paymentservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.skillbox.paymentservice.dto.ErrorDto;
import ru.skillbox.paymentservice.exception.BalanceExistsException;
import ru.skillbox.paymentservice.exception.BalanceNotFoundException;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler({BalanceNotFoundException.class, MethodArgumentNotValidException.class,
            BalanceExistsException.class})
    public ResponseEntity<ErrorDto> exceptionHandler(Exception ex) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.badRequest().body(errorDto);
    }
}