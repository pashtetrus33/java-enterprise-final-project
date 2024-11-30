package ru.skillbox.orderservice.service;

import ru.skillbox.orderservice.dto.PaymentKafkaDto;

public interface KafkaService {

    void produce(PaymentKafkaDto paymentKafkaDto);
}
