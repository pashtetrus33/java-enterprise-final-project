package service;

import config.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.skillbox.paymentservice.dto.ErrorKafkaDto;
import ru.skillbox.paymentservice.dto.OrderDto;
import ru.skillbox.paymentservice.dto.PaymentKafkaDto;
import ru.skillbox.paymentservice.dto.StatusDto;
import ru.skillbox.paymentservice.dto.enums.OrderStatus;
import ru.skillbox.paymentservice.dto.enums.ServiceName;
import ru.skillbox.paymentservice.model.Balance;
import ru.skillbox.paymentservice.model.PaymentDetails;
import ru.skillbox.paymentservice.repository.BalanceRepository;
import ru.skillbox.paymentservice.repository.PaymentDetailsRepository;
import ru.skillbox.paymentservice.service.PaymentService;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)  // Опционально для контроля порядка тестов
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private PaymentDetailsRepository paymentDetailsRepository;

    private OrderDto orderDto;
    private PaymentKafkaDto paymentKafkaDto;

    @BeforeEach
    void setUp() {
        orderDto = new OrderDto("product", "testDepartureAddress", "testDestinationAddress", 100L, 2);
        paymentKafkaDto = new PaymentKafkaDto();
        paymentKafkaDto.setUserId(1L);
        paymentKafkaDto.setOrderId(1L);
        paymentKafkaDto.setOrderDto(orderDto);
        paymentKafkaDto.setAuthHeaderValue("test header value.");
    }

    @Test
    void payTestWithoutException() {
        Balance balance = new Balance();
        balance.setBalance(1000L);
        balance.setUserId(1L);

        when(balanceRepository.findBalanceByUserId(1L)).thenReturn(Optional.of(balance));

        assertDoesNotThrow(() -> paymentService.pay(paymentKafkaDto));
        verify(balanceRepository).findBalanceByUserId(1L);
    }

    @Test
    void payTestThrowException() {
        when(balanceRepository.findBalanceByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> paymentService.pay(paymentKafkaDto));
        verify(balanceRepository).findBalanceByUserId(1L);
    }

    @Test
    void resetPaymentThrowException() {
        StatusDto statusDto = new StatusDto();
        statusDto.setComment("test comment.");
        statusDto.setStatus(OrderStatus.INVENTMENT_FAILED);
        statusDto.setServiceName(ServiceName.INVENTORY_SERVICE);

        ErrorKafkaDto errorKafkaDto = new ErrorKafkaDto();
        errorKafkaDto.setOrderId(1L);
        errorKafkaDto.setStatusDto(statusDto);

        when(paymentDetailsRepository.findByOrderId(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> paymentService.resetPayment(errorKafkaDto));
        verify(paymentDetailsRepository).findByOrderId(1L);
    }

    @Test
    void resetPaymentWithoutException() {
        StatusDto statusDto = new StatusDto();
        statusDto.setComment("test comment.");
        statusDto.setStatus(OrderStatus.INVENTMENT_FAILED);
        statusDto.setServiceName(ServiceName.INVENTORY_SERVICE);

        ErrorKafkaDto errorKafkaDto = new ErrorKafkaDto();
        errorKafkaDto.setOrderId(1L);
        errorKafkaDto.setStatusDto(statusDto);

        Balance balance = new Balance();
        balance.setUserId(1L);
        balance.setBalance(1000L);

        PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.setOrderId(1L);
        paymentDetails.setSum(20L);
        paymentDetails.setBalance(balance);

        when(paymentDetailsRepository.findByOrderId(1L)).thenReturn(Optional.of(paymentDetails));

        assertDoesNotThrow(() -> paymentService.resetPayment(errorKafkaDto));
        verify(paymentDetailsRepository).findByOrderId(1L);
    }
}