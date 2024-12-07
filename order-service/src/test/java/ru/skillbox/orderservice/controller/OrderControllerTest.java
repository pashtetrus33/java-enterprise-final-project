package ru.skillbox.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.skillbox.orderservice.dto.OrderDto;
import ru.skillbox.orderservice.dto.StatusDto;
import ru.skillbox.orderservice.dto.enums.OrderStatus;
import ru.skillbox.orderservice.dto.enums.ServiceName;
import ru.skillbox.orderservice.model.Order;
import ru.skillbox.orderservice.service.OrderService;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderDto orderDto;
    private StatusDto statusDto;
    private Order order;

    @Configuration
    @ComponentScan(basePackageClasses = {OrderController.class})
    public static class TestConf {
    }

    @BeforeEach
    void setUp() {
        // Инициализация общих тестовых данных
        orderDto = new OrderDto(
                "product",
                "testDepartureAddress",
                "testDestinationAddress",
                100L,
                2
        );

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
    void addOrderSuccessTest() throws Exception {
        // Мокирование поведения сервиса
        when(orderService.addOrder(eq(orderDto), any())).thenReturn(Optional.of(order));

        // Выполнение запроса и проверка ответа
        mockMvc.perform(
                        post("/order")
                                .header("id", 1L)
                                .content(objectMapper.writeValueAsString(orderDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cost").value(order.getCost()))
                .andExpect(jsonPath("$.status").value(order.getStatus().toString()));
    }

    @Test
    void updateOrderStatusSuccessTest() throws Exception {
        // Мокирование поведения сервиса
        doNothing().when(orderService).updateOrderStatus(1L, statusDto);

        // Выполнение запроса и проверка ответа
        mockMvc.perform(
                        patch("/order/1")
                                .content(objectMapper.writeValueAsString(statusDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    void updateOrderStatusErrorTest() throws Exception {
        // Мокирование выброса исключения
        doThrow(new OrderNotFoundException(2L)).when(orderService).updateOrderStatus(2L, statusDto);

        // Выполнение запроса и проверка ответа
        mockMvc.perform(
                        patch("/order/2")
                                .content(objectMapper.writeValueAsString(statusDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }
}