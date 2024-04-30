package wdsjk.project.avitobalancemicroservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import wdsjk.project.avitobalancemicroservice.dto.exception.Reason;
import wdsjk.project.avitobalancemicroservice.dto.request.DepositRequest;
import wdsjk.project.avitobalancemicroservice.dto.response.BalanceResponse;
import wdsjk.project.avitobalancemicroservice.service.BalanceService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BalanceController.class)
class BalanceControllerTest {
    private static final String DEFAULT_PATH = "/api/v1/balance";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    BalanceService balanceService;

    @Test
    public void testDepositShouldReturnOk200() throws Exception {
        DepositRequest requestBodyDTO = new DepositRequest(
                UUID.randomUUID().toString(),
                BigDecimal.valueOf(1000.15)
        );
        String requestBody = objectMapper.writeValueAsString(requestBodyDTO);

        BalanceResponse responseBodyDTO = new BalanceResponse("Money has been successfully deposited");
        String responseBody = objectMapper.writeValueAsString(responseBodyDTO);

        Mockito.when(balanceService.deposit(requestBodyDTO)).thenReturn(responseBodyDTO);

        mockMvc.perform(post(DEFAULT_PATH + "/deposit").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(responseBody))
                .andDo(System.out::print);
    }

    @Test
    public void testDepositShouldReturnBadRequest400() throws Exception {
        DepositRequest requestBodyDTO = new DepositRequest(
                "",
                BigDecimal.valueOf(1000.15)
        );
        String requestBody = objectMapper.writeValueAsString(requestBodyDTO);

        Reason reason = new Reason("Invalid data type!");
        String responseBody = objectMapper.writeValueAsString(reason);

        Mockito.when(balanceService.deposit(requestBodyDTO)).thenThrow();

        mockMvc.perform(post(DEFAULT_PATH + "/deposit").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(responseBody))
                .andDo(System.out::print);
    }
}