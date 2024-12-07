package config.service;

import config.TestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.skillbox.deliveryservice.dto.DeliveryKafkaDto;
import ru.skillbox.deliveryservice.dto.OrderDto;
import ru.skillbox.deliveryservice.exception.DeliveryNotFoundException;
import ru.skillbox.deliveryservice.model.Delivery;
import ru.skillbox.deliveryservice.repository.DeliveryRepository;
import ru.skillbox.deliveryservice.service.DeliveryService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
class DeliveryServiceTest {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private DeliveryService deliveryService;

    @Test
    void makeDeliveryThrowExceptionTest() {

        OrderDto orderDto = new OrderDto(
                "product",
                "testDepartureAddress",
                "testDestinationAddress",
                100L,
                2
        );

        DeliveryKafkaDto deliveryKafkaDto = new DeliveryKafkaDto();
        deliveryKafkaDto.setOrderId(1L);
        deliveryKafkaDto.setOrderDto(orderDto);
        deliveryKafkaDto.setInvoiceId(1L);
        deliveryKafkaDto.setAuthHeaderValue("test auth header value");

        doThrow(new RuntimeException("Undefined exception.")).when(deliveryRepository).save(Mockito.any(Delivery.class));
        assertThrows(RuntimeException.class, () -> deliveryService.makeDelivery(deliveryKafkaDto));
    }

    @Test
    void makeDeliveryWithoutExceptionTest() {

        OrderDto orderDto = new OrderDto(
                "product",
                "testDepartureAddress",
                "testDestinationAddress",
                100L,
                2
        );

        DeliveryKafkaDto deliveryKafkaDto = new DeliveryKafkaDto();
        deliveryKafkaDto.setOrderId(1L);
        deliveryKafkaDto.setOrderDto(orderDto);
        deliveryKafkaDto.setInvoiceId(1L);
        deliveryKafkaDto.setAuthHeaderValue("test auth header value");

        Delivery delivery = new Delivery();
        delivery.setDestinationAddress(delivery.getDestinationAddress());
        delivery.setId(1L);
        delivery.setInvoiceId(1L);

        when(deliveryRepository.save(Mockito.any(Delivery.class))).thenReturn(delivery);
        assertDoesNotThrow(() -> deliveryService.makeDelivery(deliveryKafkaDto));
    }

    @Test
    void deleteDeliveryByIdThrowExceptionTest() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(DeliveryNotFoundException.class, () -> deliveryService.deleteDeliveryById(1L));
    }

    @Test
    void deleteDeliveryByIdWithoutExceptionTest() {
        Delivery delivery = new Delivery();
        delivery.setDestinationAddress(delivery.getDestinationAddress());
        delivery.setId(1L);
        delivery.setInvoiceId(1L);

        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));
        assertDoesNotThrow(() -> deliveryService.deleteDeliveryById(1L));
    }
}