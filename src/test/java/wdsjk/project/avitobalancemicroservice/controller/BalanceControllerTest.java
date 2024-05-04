package wdsjk.project.avitobalancemicroservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.transaction.TransactionSystemException;
import wdsjk.project.avitobalancemicroservice.domain.Balance;
import wdsjk.project.avitobalancemicroservice.dto.exception.Reason;
import wdsjk.project.avitobalancemicroservice.dto.request.DepositRequest;
import wdsjk.project.avitobalancemicroservice.dto.request.WithdrawRequest;
import wdsjk.project.avitobalancemicroservice.dto.response.BalanceResponse;
import wdsjk.project.avitobalancemicroservice.repository.BalanceRepository;
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

    static final BalanceResponse balanceResponse = new BalanceResponse("Money has been successfully deposited");

    @Test
    public void testDepositShouldReturnOk200() throws Exception {
        // creating request
        DepositRequest requestDTO = new DepositRequest(
                UUID.randomUUID().toString(),
                BigDecimal.valueOf(1000.15)
        );
        // writing request as string for asserting
        String request = objectMapper.writeValueAsString(requestDTO);

        // response
        String response = objectMapper.writeValueAsString(balanceResponse);

        // performing balanceService work
        Mockito.when(balanceService.deposit(requestDTO)).thenReturn(balanceResponse);

        // performing request and asserting response
        mockMvc.perform(post(DEFAULT_PATH + "/deposit").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(response))
                .andDo(System.out::print);
    }

    @Test
    public void testDepositShouldReturnBadRequest400() throws Exception {
        // requests
        String requestBlankUserId = objectMapper.writeValueAsString(
                new DepositRequest("", BigDecimal.valueOf(1000.15))
        );
        // using JSONObject.stringToValue because DepositRequest constructor doesn't allow to put int in userId field
        String requestInvalidDataType = objectMapper.writeValueAsString(
                JSONObject.stringToValue("{userId: 12, amountOfMoney: 1000.15}")
        );
        String requestMoreThanOnBalance = objectMapper.writeValueAsString(
                new DepositRequest("123", BigDecimal.valueOf(-1000))
        );

        // responses
        String responseBlankUserId = objectMapper.writeValueAsString(
                new Reason("userId: Can't be blank!")
        );
        String responseInvalidDataType = objectMapper.writeValueAsString(
                new Reason("Invalid data type!")
        );
        String responseMoreThanOnBalance = objectMapper.writeValueAsString(
                new Reason("You're trying to withdraw more than is in your account!")
        );

        // performing balanceService work
        Mockito.when(balanceService.deposit(new DepositRequest("123", BigDecimal.valueOf(-1000)))).thenThrow(TransactionSystemException.class);

        // performing requests and asserting responses
        mockMvc.perform(post(DEFAULT_PATH + "/deposit").contentType(MediaType.APPLICATION_JSON).content(requestBlankUserId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(responseBlankUserId))
                .andDo(System.out::print);

        mockMvc.perform(post(DEFAULT_PATH + "/deposit").contentType(MediaType.APPLICATION_JSON).content(requestInvalidDataType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(responseInvalidDataType))
                .andDo(System.out::print);

        mockMvc.perform(post(DEFAULT_PATH + "/deposit").contentType(MediaType.APPLICATION_JSON).content(requestMoreThanOnBalance))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(responseMoreThanOnBalance))
                .andDo(System.out::print);
    }

    @Test
    public void testWithdrawShouldReturnOk200() throws Exception {
        String request = objectMapper.writeValueAsString(new WithdrawRequest("123", BigDecimal.valueOf(100)));

        String response = objectMapper.writeValueAsString(balanceResponse);

        Mockito.when(balanceService.withdraw(new WithdrawRequest("123", BigDecimal.valueOf(100)))).thenReturn(balanceResponse);

        mockMvc.perform(post(DEFAULT_PATH + "/withdraw").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(response))
                .andDo(System.out::print);
    }
}