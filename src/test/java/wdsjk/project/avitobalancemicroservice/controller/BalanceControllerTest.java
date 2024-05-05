package wdsjk.project.avitobalancemicroservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.transaction.TransactionSystemException;
import wdsjk.project.avitobalancemicroservice.dto.exception.Reason;
import wdsjk.project.avitobalancemicroservice.dto.request.DepositRequest;
import wdsjk.project.avitobalancemicroservice.dto.request.TransferRequest;
import wdsjk.project.avitobalancemicroservice.dto.request.WithdrawRequest;
import wdsjk.project.avitobalancemicroservice.dto.response.BalanceResponse;
import wdsjk.project.avitobalancemicroservice.exception.UserNotFoundException;
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

    static final BalanceResponse balanceResponseOk200 = new BalanceResponse("Money has been successfully deposited");
    static final BalanceResponse transferResponseOk200 = new BalanceResponse("Money has been successfully transferred");

    @Test
    public void testDepositShouldReturnOk200() throws Exception {
        // creating request
        DepositRequest depositRequest = new DepositRequest(UUID.randomUUID().toString(), BigDecimal.valueOf(1000.15));

        // writing request as string for asserting
        String request = objectMapper.writeValueAsString(depositRequest);

        // response
        String response = objectMapper.writeValueAsString(balanceResponseOk200);

        // performing balanceService work
        Mockito.when(balanceService.deposit(depositRequest)).thenReturn(balanceResponseOk200);

        // performing request and asserting response
        mockMvc.perform(post(DEFAULT_PATH + "/deposit").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(response))
                .andDo(System.out::print);
    }

    @Test
    public void testDepositShouldReturnBadRequest400() throws Exception {
        //requests
        DepositRequest requestForBlankUserId = new DepositRequest("", BigDecimal.valueOf(1000.15));
        DepositRequest requestForMoreThanOnBalance = new DepositRequest("123", BigDecimal.valueOf(-1000));

        // writing requests as strings for asserting
        String requestBlankUserId = objectMapper.writeValueAsString(requestForBlankUserId);
        String requestMoreThanOnBalance = objectMapper.writeValueAsString(requestForMoreThanOnBalance);
        // using JSONObject.stringToValue because DepositRequest constructor doesn't allow to put int in userId field
        String requestInvalidDataType = objectMapper.writeValueAsString(JSONObject.stringToValue("{userId: 12, amountOfMoney: 1000.15}"));

        // responses
        String responseBlankUserId = objectMapper.writeValueAsString(new Reason("userId: Can't be blank!"));
        String responseMoreThanOnBalance = objectMapper.writeValueAsString(new Reason("You're trying to withdraw more than is in your account!"));
        String responseInvalidDataType = objectMapper.writeValueAsString(new Reason("Invalid data type!"));

        // performing balanceService work
        Mockito.when(balanceService.deposit(requestForMoreThanOnBalance)).thenThrow(TransactionSystemException.class);

        // performing requests and asserting responses
        mockMvc.perform(post(DEFAULT_PATH + "/deposit").contentType(MediaType.APPLICATION_JSON).content(requestBlankUserId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(responseBlankUserId))
                .andDo(System.out::print);

        mockMvc.perform(post(DEFAULT_PATH + "/deposit").contentType(MediaType.APPLICATION_JSON).content(requestMoreThanOnBalance))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(responseMoreThanOnBalance))
                .andDo(System.out::print);

        mockMvc.perform(post(DEFAULT_PATH + "/deposit").contentType(MediaType.APPLICATION_JSON).content(requestInvalidDataType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(responseInvalidDataType))
                .andDo(System.out::print);
    }

    @Test
    public void testWithdrawShouldReturnOk200() throws Exception {
        WithdrawRequest withdrawRequest = new WithdrawRequest("123", BigDecimal.valueOf(100));

        String request = objectMapper.writeValueAsString(withdrawRequest);

        String response = objectMapper.writeValueAsString(balanceResponseOk200);

        Mockito.when(balanceService.withdraw(withdrawRequest)).thenReturn(balanceResponseOk200);

        mockMvc.perform(post(DEFAULT_PATH + "/withdraw").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(response))
                .andDo(System.out::print);
    }

    @Test
    public void testWithdrawShouldReturnBadRequest400() throws Exception {
        String requestBlankUserId = objectMapper.writeValueAsString(new WithdrawRequest("", BigDecimal.valueOf(100)));
        String requestInvalidAmountOfMoney = objectMapper.writeValueAsString(new WithdrawRequest("123", BigDecimal.valueOf(-1)));
        // using JSONObject.stringToValue because WithdrawRequest constructor doesn't allow to put int in userId field
        String requestInvalidDataType = objectMapper.writeValueAsString(JSONObject.stringToValue("{userId: 12, amountOfMoney: 100}"));

        String responseBlankUserId = objectMapper.writeValueAsString(new Reason("userId: Can't be blank!"));
        String responseInvalidAmountOfMoney = objectMapper.writeValueAsString(new Reason("amountOfMoney: Can't be negative or equals to 0!"));
        String responseInvalidDataType = objectMapper.writeValueAsString(new Reason("Invalid data type!"));

        mockMvc.perform(post(DEFAULT_PATH + "/withdraw").contentType(MediaType.APPLICATION_JSON).content(requestBlankUserId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(responseBlankUserId))
                .andDo(System.out::print);

        mockMvc.perform(post(DEFAULT_PATH + "/withdraw").contentType(MediaType.APPLICATION_JSON).content(requestInvalidAmountOfMoney))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(responseInvalidAmountOfMoney))
                .andDo(System.out::print);

        mockMvc.perform(post(DEFAULT_PATH + "/withdraw").contentType(MediaType.APPLICATION_JSON).content(requestInvalidDataType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(responseInvalidDataType))
                .andDo(System.out::print);
    }

    @Test
    public void testTransferShouldReturnOk200() throws Exception {
        TransferRequest transferRequest = new TransferRequest("123", "456", BigDecimal.valueOf(100));

        String request = objectMapper.writeValueAsString(transferRequest);

        String response = objectMapper.writeValueAsString(transferResponseOk200);

        Mockito.when(balanceService.transfer(transferRequest)).thenReturn(transferResponseOk200);

        mockMvc.perform(post(DEFAULT_PATH + "/transfer").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(response))
                .andDo(System.out::print);
    }

    @Test
    public void testTransferShouldReturnBadRequest400() throws Exception {
        TransferRequest transferForUserIdNotFound = new TransferRequest("123", "456", BigDecimal.valueOf(100));
        TransferRequest transferForBlankUserId = new TransferRequest("", "456", BigDecimal.valueOf(100));
        TransferRequest transferForInvalidAmountOfMoney = new TransferRequest("123", "456", BigDecimal.valueOf(-1));
        TransferRequest transferForMoreThanOnBalance = new TransferRequest("123", "456", BigDecimal.valueOf(1000000));

        String requestUserIdNotFound = objectMapper.writeValueAsString(transferForUserIdNotFound);
        String requestBlankUserId = objectMapper.writeValueAsString(transferForBlankUserId);
        String requestInvalidAmountOfMoney = objectMapper.writeValueAsString(transferForInvalidAmountOfMoney); // checked for 0 too
        String requestMoreThanOnBalance = objectMapper.writeValueAsString(transferForMoreThanOnBalance);
        String requestInvalidDataType = objectMapper.writeValueAsString(JSONObject.stringToValue("{userFromId: 12, userToId: \"1\", amountOfMoney: 100}"));

        String responseUserIdNotFound = objectMapper.writeValueAsString(new Reason("User with id: 123 is not found!"));
        String responseBlankUserId = objectMapper.writeValueAsString(new Reason("userFromId: Can't be blank!"));
        String responseInvalidAmountOfMoney = objectMapper.writeValueAsString(new Reason("amountOfMoney: Can't be negative or equals to 0!"));
        String responseMoreThanOnBalance = objectMapper.writeValueAsString(new Reason("You're trying to withdraw more than is in your account!"));
        String responseInvalidDataType = objectMapper.writeValueAsString(new Reason("Invalid data type!"));

        Mockito.when(balanceService.transfer(transferForUserIdNotFound)).thenThrow(new UserNotFoundException("User with id: 123 is not found!"));
        Mockito.when(balanceService.transfer(transferForMoreThanOnBalance)).thenThrow(TransactionSystemException.class);

        mockMvc.perform(post(DEFAULT_PATH + "/transfer").contentType(MediaType.APPLICATION_JSON).content(requestUserIdNotFound))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(responseUserIdNotFound))
                .andDo(System.out::print);

        mockMvc.perform(post(DEFAULT_PATH + "/transfer").contentType(MediaType.APPLICATION_JSON).content(requestBlankUserId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(responseBlankUserId))
                .andDo(System.out::print);

        mockMvc.perform(post(DEFAULT_PATH + "/transfer").contentType(MediaType.APPLICATION_JSON).content(requestInvalidAmountOfMoney))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(responseInvalidAmountOfMoney))
                .andDo(System.out::print);

        mockMvc.perform(post(DEFAULT_PATH + "/transfer").contentType(MediaType.APPLICATION_JSON).content(requestMoreThanOnBalance))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(responseMoreThanOnBalance))
                .andDo(System.out::print);

        mockMvc.perform(post(DEFAULT_PATH + "/transfer").contentType(MediaType.APPLICATION_JSON).content(requestInvalidDataType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(responseInvalidDataType))
                .andDo(System.out::print);
    }
}