package ru.skillbox.paymentservice.service;

public interface KafkaService {

    void produce(Object kafkaDto);
}