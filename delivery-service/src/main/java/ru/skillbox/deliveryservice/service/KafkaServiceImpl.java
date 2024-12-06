package ru.skillbox.deliveryservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.skillbox.deliveryservice.dto.ErrorKafkaDto;

@Service
public class KafkaServiceImpl implements KafkaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaServiceImpl.class);

    @Value("${spring.kafka.order-service-topic}")
    private String kafkaOrderServiceTopic;

    @Value("${spring.kafka.error-inventory-service-topic}")
    private String kafkaErrorInventoryServiceTopic;

    private final KafkaTemplate<Long, Object> kafkaTemplate;

    @Autowired
    public KafkaServiceImpl(KafkaTemplate<Long, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void produce(Object kafkaDto) {
        if (kafkaDto instanceof ErrorKafkaDto) {
            kafkaTemplate.send(kafkaErrorInventoryServiceTopic, kafkaDto);
        } else {
            kafkaTemplate.send(kafkaOrderServiceTopic, kafkaDto);
        }
        LOGGER.info("Sent message to Kafka -> '{}'", kafkaDto);
    }
}