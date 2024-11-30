package ru.skillbox.paymentservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.skillbox.paymentservice.dto.ErrorKafkaDto;

@Service
public class KafkaServiceImpl implements KafkaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaServiceImpl.class);

    @Value("${spring.kafka.inventory-service-topic}")
    private String kafkaInventoryServiceTopic;

    @Value("${spring.kafka.error-order-service-topic}")
    private String kafkaErrorOrderServiceTopic;

    private final KafkaTemplate<Long, Object> kafkaTemplate;

    public KafkaServiceImpl(KafkaTemplate<Long, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void produce(Object kafkaDto) {
        if (kafkaDto instanceof ErrorKafkaDto) {
            kafkaTemplate.send(kafkaErrorOrderServiceTopic, kafkaDto);
        } else {
            kafkaTemplate.send(kafkaInventoryServiceTopic, kafkaDto);
        }
        LOGGER.info("Sent message to Kafka -> '{}'", kafkaDto);
    }
}