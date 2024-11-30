package ru.skillbox.paymentservice.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.skillbox.paymentservice.dto.ErrorKafkaDto;
import ru.skillbox.paymentservice.dto.PaymentKafkaDto;
import ru.skillbox.paymentservice.service.PaymentService;

@Component
public class PaymentServiceConsumer {

    private final PaymentService paymentService;
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceConsumer.class);

    @Autowired
    public PaymentServiceConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "${spring.kafka.payment-service-topic}",
            groupId = "payment-service-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeFromOrderService(PaymentKafkaDto paymentKafkaDto) {
        LOGGER.info("Consumed message from Kafka -> '{}'", paymentKafkaDto);
        paymentService.pay(paymentKafkaDto);
    }


    @KafkaListener(topics = "${spring.kafka.error-payment-service-topic}",
            groupId = "payment-service-group",
            containerFactory = "kafkaListenerContainerErrorFactory")
    public void consumeFromInventoryService(ErrorKafkaDto errorKafkaDto) {
        LOGGER.info("Consumed error message from Kafka -> '{}'", errorKafkaDto);
        paymentService.resetPayment(errorKafkaDto);
    }
}