package ru.skillbox.deliveryservice.service;

import ru.skillbox.deliveryservice.dto.DeliveryKafkaDto;
import ru.skillbox.deliveryservice.exception.DeliveryNotFoundException;

public interface DeliveryService {

    void makeDelivery(DeliveryKafkaDto deliveryKafkaDto);

    void deleteDeliveryById(Long deliveryId) throws DeliveryNotFoundException;
}