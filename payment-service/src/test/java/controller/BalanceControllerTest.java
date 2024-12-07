package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.skillbox.paymentservice.controller.BalanceController;
import ru.skillbox.paymentservice.dto.SumDto;
import ru.skillbox.paymentservice.exception.BalanceExistsException;
import ru.skillbox.paymentservice.exception.BalanceNotFoundException;
import ru.skillbox.paymentservice.model.Balance;
import ru.skillbox.paymentservice.service.BalanceService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(BalanceController.class)
class BalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BalanceService balanceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Configuration
    @ComponentScan(basePackageClasses = {BalanceController.class})
    public static class TestConf {
    }

    @Test
    void createPaymentAccountErrorTest() throws Exception {
        doThrow(new BalanceExistsException("A balance account for the specified user already exists."))
                .when(balanceService).createBalance(1L);
        mockMvc.perform(
                        post("/balance")
                                .with(request -> {
                                    request.addHeader("id", 1L);
                                    return request;
                                })
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPaymentAccountSuccessTest() throws Exception {
        Balance balance = new Balance();
        balance.setId(1L);
        balance.setBalance(1000L);
        balance.setUserId(1L);

        when(balanceService.createBalance(1L)).thenReturn(balance);
        mockMvc.perform(
                        post("/balance")
                                .with(request -> {
                                    request.addHeader("id", 1L);
                                    return request;
                                })
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1));
    }

    @Test
    void replenishBalanceErrorTest() throws Exception {
        SumDto sumDto = new SumDto();
        sumDto.setSum(100);

        doThrow(new BalanceNotFoundException("Balance with ID 2 not found."))
                .when(balanceService).replenishBalance(1L, sumDto);
        mockMvc.perform(
                        patch("/balance/1")
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(sumDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void replenishBalanceSuccessTest() throws Exception {
        SumDto sumDto = new SumDto();
        sumDto.setSum(100);

        doNothing().when(balanceService).replenishBalance(1L, sumDto);
        mockMvc.perform(
                        patch("/balance/1")
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(sumDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }
}