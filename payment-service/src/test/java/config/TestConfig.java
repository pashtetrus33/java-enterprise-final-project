package config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.skillbox.paymentservice.repository.BalanceRepository;
import ru.skillbox.paymentservice.repository.PaymentDetailsRepository;
import ru.skillbox.paymentservice.service.KafkaService;
import ru.skillbox.paymentservice.service.PaymentService;
import ru.skillbox.paymentservice.service.PaymentServiceImpl;
import ru.skillbox.paymentservice.service.RequestSendingService;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    public KafkaService kafkaServiceMock() {
        return mock(KafkaService.class);
    }

    @Bean
    public BalanceRepository balanceRepositoryMock(){
        return mock(BalanceRepository.class);
    }

    @Bean
    public PaymentDetailsRepository paymentDetailsRepositoryMock() {
        return mock(PaymentDetailsRepository.class);
    }

    @Bean
    public RequestSendingService requestSendingServiceMock() {
        return mock(RequestSendingService.class);
    }

    @Bean
    public PaymentService paymentService() {
        return new PaymentServiceImpl(balanceRepositoryMock(), paymentDetailsRepositoryMock(),
                kafkaServiceMock(), requestSendingServiceMock());
    }
}