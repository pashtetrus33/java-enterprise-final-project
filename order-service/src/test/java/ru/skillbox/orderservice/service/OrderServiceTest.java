package ru.skillbox.orderservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.skillbox.orderservice.config.TestConfig;
import ru.skillbox.orderservice.controller.OrderNotFoundException;
import ru.skillbox.orderservice.dto.OrderDto;
import ru.skillbox.orderservice.dto.StatusDto;
import ru.skillbox.orderservice.dto.enums.OrderStatus;
import ru.skillbox.orderservice.dto.enums.ServiceName;
import ru.skillbox.orderservice.model.Order;
import ru.skillbox.orderservice.repository.OrderRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    private OrderDto orderDto;
    private MockHttpServletRequest mockHttpServletRequest;
    private StatusDto statusDto;
    private Order order;

    @BeforeEach
    void setUp() {
        // Создание тестовых данных
        orderDto = new OrderDto(
                "product",
                "testDepartureAddress",
                "testDestinationAddress",
                100L,
                2
        );

        mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("id", 1L);

        statusDto = new StatusDto();
        statusDto.setStatus(OrderStatus.INVENTED);
        statusDto.setComment("Order has been paid.");

        order = new Order();
        order.setCost(100L);
        order.setStatus(OrderStatus.REGISTERED);
        order.setDestinationAddress("test address");
        order.addStatusHistory(OrderStatus.REGISTERED, ServiceName.ORDER_SERVICE,
                "The order has been registered.");
    }

    @Test
    void addOrderSuccessTest() {
        // Мокирование поведения репозитория
        when(orderRepository.save(Mockito.any(Order.class))).thenReturn(order);

        // Выполнение тестируемого метода и проверка результата
        assertThat(orderService.addOrder(orderDto, mockHttpServletRequest).get().getCost())
                .isEqualTo(order.getCost());
    }

    @Test
    void updateOrderStatusShouldThrowExceptionWhenOrderNotFound() {
        // Мокирование отсутствия заказа в репозитории
        when(orderRepository.findById(2L)).thenReturn(Optional.empty());

        // Проверка выброса исключения
        assertThrows(OrderNotFoundException.class,
                () -> orderService.updateOrderStatus(2L, statusDto));
    }

    @Test
    void updateOrderStatusShouldNotThrowExceptionWhenOrderExists() {
        // Мокирование наличия заказа в репозитории
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Проверка, что исключение не выбрасывается
        assertDoesNotThrow(() -> orderService.updateOrderStatus(1L, statusDto));
    }
}