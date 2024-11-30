package ru.skillbox.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.skillbox.orderservice.dto.PaymentKafkaDto;

@Service
@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaServiceImpl.class);

    @Value("${spring.kafka.payment-service-topic}")
    private String kafkaTopic;

    private final KafkaTemplate<Long, PaymentKafkaDto> kafkaTemplate;


    @Override
    public void produce(PaymentKafkaDto kafkaDto) {
        kafkaTemplate.send(kafkaTopic, kafkaDto);
        logger.info("Sent message to Kafka -> '{}'", kafkaDto);
    }
}