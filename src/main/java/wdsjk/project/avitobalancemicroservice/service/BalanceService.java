package wdsjk.project.avitobalancemicroservice.service;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.util.JSONPObject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.stereotype.Service;

import wdsjk.project.avitobalancemicroservice.domain.Balance;
import wdsjk.project.avitobalancemicroservice.dto.ShowRequest;
import wdsjk.project.avitobalancemicroservice.dto.request.DepositRequest;
import wdsjk.project.avitobalancemicroservice.dto.request.TransferRequest;
import wdsjk.project.avitobalancemicroservice.dto.response.BalanceResponse;
import wdsjk.project.avitobalancemicroservice.dto.request.WithdrawRequest;

import wdsjk.project.avitobalancemicroservice.exception.InternalErrorException;
import wdsjk.project.avitobalancemicroservice.exception.UserNotFoundException;
import wdsjk.project.avitobalancemicroservice.repository.BalanceRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BalanceService {
    private final BalanceRepository balanceRepository;

    public BalanceResponse deposit(DepositRequest request) {
        Balance balance = balanceRepository.findByUserId(request.userId()).orElse(null);

        if (null != balance) {
            balance.setAmountOfMoney(balance.getAmountOfMoney().add(request.amountOfMoney()).setScale(2, RoundingMode.HALF_DOWN));
        } else {
            balance = Balance.builder()
                        .id(UUID.randomUUID().toString())
                        .userId(request.userId())
                        .amountOfMoney(request.amountOfMoney().setScale(2, RoundingMode.HALF_DOWN))
                    .build();
        }

        balanceRepository.save(balance);

        return new BalanceResponse("Money has been successfully deposited");
    }

    @Transactional
    public BalanceResponse withdraw(WithdrawRequest request) {
        Balance balance = balanceRepository.findByUserId(request.userId()).orElseThrow(
                () -> new UserNotFoundException(String.format("User with id: %s is not found!", request.userId()))
        );

        balance.setAmountOfMoney(balance.getAmountOfMoney().subtract(request.amountOfMoney()).setScale(2, RoundingMode.HALF_DOWN));

        return new BalanceResponse("Money has been successfully withdrew");
    }

    @Transactional
    public BalanceResponse transfer(TransferRequest request) {
        Balance balanceFrom = balanceRepository.findByUserId(request.userFromId()).orElseThrow(
                () -> new UserNotFoundException(String.format("User with id: %s is not found!", request.userFromId()))
        );
        Balance balanceTo = balanceRepository.findByUserId(request.userToId()).orElseThrow(
                () -> new UserNotFoundException(String.format("User with id: %s is not found!", request.userToId()))
        );

        balanceFrom.setAmountOfMoney(balanceFrom.getAmountOfMoney().subtract(request.amountOfMoney()).setScale(2, RoundingMode.HALF_DOWN));
        balanceTo.setAmountOfMoney(balanceTo.getAmountOfMoney().add(request.amountOfMoney()).setScale(2, RoundingMode.HALF_DOWN));

        return new BalanceResponse("Money has been successfully transferred");
    }

    public BalanceResponse show(ShowRequest request, String currency) {
        BigDecimal amount = balanceRepository.findByUserId(request.userId()).orElseThrow(
                () -> new UserNotFoundException(String.format("User with id: %s is not found!", request.userId()))
        ).getAmountOfMoney();

        if (null != currency) {
            try(HttpClient client = HttpClient.newBuilder().build()) {
                HttpRequest req = HttpRequest.newBuilder(URI.create(
                        String.format("https://api.freecurrencyapi.com/v1/latest?apikey=fca_live_B2FeYk8cwUtZhT2Q0CNSZSfuswYpsLMk3yfMwTQb&" +
                                "base_currency=RUB&" +
                                "currencies=%s", currency)
                )).build();

                HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());

                JSONObject jsonObject = new JSONObject(response.body());

                if (!jsonObject.isNull("data"))
                    // TODO: Redis for caching exchange rates
                    amount = amount.multiply(jsonObject.getJSONObject("data").getBigDecimal(currency)).setScale(2, RoundingMode.HALF_DOWN);
            } catch (InterruptedException | IOException e) {
                throw new InternalErrorException("Something went wrong on the server side! Please, try again later");
            }
        }

        return new BalanceResponse(String.valueOf(amount));
    }
}
