package config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.skillbox.inventoryservice.repository.InventoryRepository;
import ru.skillbox.inventoryservice.repository.InvoiceInventoryRepository;
import ru.skillbox.inventoryservice.repository.InvoiceRepository;
import ru.skillbox.inventoryservice.service.InventoryService;
import ru.skillbox.inventoryservice.service.InventoryServiceImpl;
import ru.skillbox.inventoryservice.service.KafkaService;
import ru.skillbox.inventoryservice.service.RequestSendingService;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    public KafkaService kafkaService() {
        return mock(KafkaService.class);
    }

    @Bean
    public RequestSendingService requestSendingService() {
        return mock(RequestSendingService.class);
    }

    @Bean
    public InvoiceInventoryRepository invoiceInventoryRepository() {
        return mock(InvoiceInventoryRepository.class);
    }

    @Bean
    public InvoiceRepository invoiceRepository() {
        return mock(InvoiceRepository.class);
    }

    @Bean
    public InventoryRepository inventoryRepository() {
        return mock(InventoryRepository.class);
    }

    @Bean
    public InventoryService inventoryService() {
        return new InventoryServiceImpl(inventoryRepository(), invoiceRepository(), invoiceInventoryRepository(),
                requestSendingService(), kafkaService());
    }
}