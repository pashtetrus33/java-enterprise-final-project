package ru.skillbox.orderservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.skillbox.orderservice.repository.OrderRepository;
import ru.skillbox.orderservice.service.KafkaService;
import ru.skillbox.orderservice.service.OrderService;
import ru.skillbox.orderservice.service.OrderServiceImpl;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    public KafkaService kafkaServiceMock() {
        return mock(KafkaService.class);
    }

    @Bean
    public OrderRepository orderRepositoryMock(){
        return mock(OrderRepository.class);
    }

    @Bean
    public OrderService orderServiceTest() {
        return new OrderServiceImpl(orderRepositoryMock(), kafkaServiceMock());
    }
}