package config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.skillbox.deliveryservice.repository.DeliveryRepository;
import ru.skillbox.deliveryservice.service.DeliveryService;
import ru.skillbox.deliveryservice.service.DeliveryServiceImpl;
import ru.skillbox.deliveryservice.service.KafkaService;
import ru.skillbox.deliveryservice.service.RequestSendingService;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    public RequestSendingService requestSendingService() {
        return mock(RequestSendingService.class);
    }

    @Bean
    public KafkaService kafkaService() {
        return mock(KafkaService.class);
    }

    @Bean
    public DeliveryRepository deliveryRepository() {
        return mock(DeliveryRepository.class);
    }

    @Bean
    public DeliveryService deliveryService() {
        return new DeliveryServiceImpl(deliveryRepository(), kafkaService(), requestSendingService());
    }
}