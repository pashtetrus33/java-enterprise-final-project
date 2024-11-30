package ru.skillbox.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.skillbox.orderservice.model.*;
import ru.skillbox.orderservice.dto.OrderDto;
import ru.skillbox.orderservice.dto.StatusDto;
import ru.skillbox.orderservice.model.enums.OrderStatus;
import ru.skillbox.orderservice.model.enums.ServiceName;
import ru.skillbox.orderservice.service.OrderService;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

    @Configuration
    @ComponentScan(basePackageClasses = {OrderController.class})
    public static class TestConf {
    }

    @Test
    void addOrderSuccessTest() throws Exception {
        OrderDto orderDto = new OrderDto("product", "testDepartureAddress",
                "testDestinationAddress",100L);

        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("id", 1L);

        Order order = new Order();
        order.setCost(100L);
        order.setStatus(OrderStatus.REGISTERED);
        order.setDestinationAddress("test address");
        order.addStatusHistory(OrderStatus.REGISTERED, ServiceName.ORDER_SERVICE,
                "The order has been registered.");

        when(orderService.addOrder(orderDto, mockHttpServletRequest)).thenReturn(Optional.of(order));
        mockMvc.perform(
                        post("/order")
                                .with(request -> {
                                    request.addHeader("id", 1L);
                                    return request;
                                })
                                .content(objectMapper.writeValueAsString(orderDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated());
    }

    @Test
    void updateOrderStatusSuccessTest() throws Exception {
        StatusDto statusDto = new StatusDto();
        statusDto.setStatus(OrderStatus.INVENTED);
        statusDto.setComment("Order has been paid.");

        doNothing().when(orderService).updateOrderStatus(1L, statusDto);
        mockMvc.perform(
                        patch("/order/1")
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(statusDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    void updateOrderStatusErrorTest() throws Exception {
        StatusDto statusDto = new StatusDto();
        statusDto.setStatus(OrderStatus.INVENTED);
        statusDto.setComment("Order has been paid.");

        doThrow(new OrderNotFoundException(2L)).when(orderService).updateOrderStatus(2L, statusDto);
        mockMvc.perform(
                        patch("/order/2")
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(statusDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }
}