package ru.skillbox.deliveryservice.service;

public interface KafkaService {

    void produce(Object kafkaDto);
}