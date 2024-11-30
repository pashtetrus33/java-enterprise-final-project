package ru.skillbox.paymentservice.service;

import ru.skillbox.paymentservice.dto.ErrorKafkaDto;
import ru.skillbox.paymentservice.dto.PaymentKafkaDto;

public interface PaymentService {

    void pay(PaymentKafkaDto paymentKafkaDto);

    void resetPayment(ErrorKafkaDto errorKafkaDto);
}