package ru.skillbox.inventoryservice.service;

public interface KafkaService {

    void produce(Object kafkaDto);
}