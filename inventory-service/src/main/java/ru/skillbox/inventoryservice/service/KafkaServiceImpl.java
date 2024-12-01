package ru.skillbox.inventoryservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.skillbox.inventoryservice.dto.ErrorKafkaDto;

@Service
public class KafkaServiceImpl implements KafkaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaServiceImpl.class);

    @Value("${spring.kafka.delivery-service-topic}")
    private String kafkaDeliveryServiceTopic;

    @Value("${spring.kafka.error-payment-service-topic}")
    private String kafkaErrorPaymentServiceTopic;

    private final KafkaTemplate<Long, Object> kafkaTemplate;

    @Autowired
    public KafkaServiceImpl(KafkaTemplate<Long, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void produce(Object kafkaDto) {
        if (kafkaDto instanceof ErrorKafkaDto) {
            kafkaTemplate.send(kafkaErrorPaymentServiceTopic, kafkaDto);
        } else {
            kafkaTemplate.send(kafkaDeliveryServiceTopic, kafkaDto);
        }
        LOGGER.info("Sent message to Kafka -> '{}'", kafkaDto);
    }
}