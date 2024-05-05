package wdsjk.project.avitobalancemicroservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.json.JSONException;
import org.json.JSONObject;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import wdsjk.project.avitobalancemicroservice.domain.Balance;
import wdsjk.project.avitobalancemicroservice.domain.Transaction;

import wdsjk.project.avitobalancemicroservice.dto.request.ShowAndTransactionRequest;
import wdsjk.project.avitobalancemicroservice.dto.request.DepositRequest;
import wdsjk.project.avitobalancemicroservice.dto.request.TransferRequest;
import wdsjk.project.avitobalancemicroservice.dto.request.WithdrawRequest;

import wdsjk.project.avitobalancemicroservice.dto.response.BalanceResponse;
import wdsjk.project.avitobalancemicroservice.dto.response.TransactionResponse;

import wdsjk.project.avitobalancemicroservice.exception.InternalErrorException;
import wdsjk.project.avitobalancemicroservice.exception.UserNotFoundException;

import wdsjk.project.avitobalancemicroservice.repository.BalanceRepository;
import wdsjk.project.avitobalancemicroservice.repository.TransactionRepository;
import wdsjk.project.avitobalancemicroservice.service.BalanceService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.*;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final BalanceRepository balanceRepository;
    private final TransactionRepository transactionRepository;

    private final RedisTemplate<String, BigDecimal> redisTemplate;

    @Override
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
        transactionRepository.save(
                Transaction.builder()
                        .id(UUID.randomUUID().toString())
                        .userFromId(request.userId())
                        .userToId(request.userId())
                        .amount(request.amountOfMoney())
                        .date(new Date())
                        .comments("Deposit or withdraw to or from user's account")
                        .build()
        );

        return new BalanceResponse("Money has been successfully deposited");
    }

    @Override
    @Transactional
    public BalanceResponse withdraw(WithdrawRequest request) {
        Balance balance = balanceRepository.findByUserId(request.userId()).orElseThrow(
                () -> new UserNotFoundException(String.format("User with id: %s is not found!", request.userId()))
        );

        balance.setAmountOfMoney(balance.getAmountOfMoney().subtract(request.amountOfMoney()).setScale(2, RoundingMode.HALF_DOWN));
        transactionRepository.save(
                Transaction.builder()
                        .id(UUID.randomUUID().toString())
                        .userFromId(request.userId())
                        .userToId(request.userId())
                        .amount(request.amountOfMoney())
                        .date(new Date())
                        .comments("Deposit or withdraw to or from user's account")
                        .build()
        );

        return new BalanceResponse("Money has been successfully withdrew");
    }

    @Override
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
        transactionRepository.save(
                Transaction.builder()
                        .id(UUID.randomUUID().toString())
                        .userFromId(request.userFromId())
                        .userToId(request.userToId())
                        .amount(request.amountOfMoney())
                        .date(new Date())
                        .comments("Transfer money between users' accounts")
                        .build()
        );

        return new BalanceResponse("Money has been successfully transferred");
    }

    @Override
    public BalanceResponse show(ShowAndTransactionRequest request, String currency) {
        BigDecimal amount = balanceRepository.findByUserId(request.userId()).orElseThrow(
                () -> new UserNotFoundException(String.format("User with id: %s is not found!", request.userId()))
        ).getAmountOfMoney();

        if (null != currency) {
            BigDecimal exchangeRate = redisTemplate.opsForValue().get(currency);
            if (null == exchangeRate) {
                try {
                    HttpClient client = HttpClient.newBuilder().build();
                    HttpRequest req = HttpRequest
                            .newBuilder(
                                URI.create(
                                    String.format("https://api.freecurrencyapi.com/v1/latest?" +
                                            "base_currency=RUB&" +
                                            "currencies=%s", currency)
                                )
                            )
                            .header(
                                    "apikey", "fca_live_B2FeYk8cwUtZhT2Q0CNSZSfuswYpsLMk3yfMwTQb" // api-key from https://freecurrencyapi.com
                                                            // (better if it was in some environment variable but for learning purposes it's okay)
                            )
                            .build();

                    String response = client
                            .sendAsync(req, HttpResponse.BodyHandlers.ofString()) // sending request
                            .thenApply(HttpResponse::body) // getting body
                            .join(); // returning value on complete

                    JSONObject jsonObject = new JSONObject(response);

                    if (!jsonObject.isNull("data")) {
                        exchangeRate = jsonObject.getJSONObject("data").getBigDecimal(currency);
                        amount = amount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_DOWN);

                        redisTemplate.opsForValue().set(currency, exchangeRate, 60, TimeUnit.SECONDS); // caching with TTL = 1 minute
                    }
                } catch (JSONException e) {
                    throw new InternalErrorException();
                }
            } else
                amount = amount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_DOWN);
        }

        return new BalanceResponse(String.valueOf(amount));
    }

    @Override
    public List<TransactionResponse> transactions(ShowAndTransactionRequest request, Integer limit, String orderBy) {
        List<Transaction> transactions = transactionRepository.findAllByUserId(request.userId(), PageRequest.of(0, limit, Sort.by(orderBy))).orElseThrow(
                InternalErrorException::new
        );

        return transactions.stream().map(
                transaction -> TransactionResponse.builder()
                        .userFromId(transaction.getUserFromId())
                        .userToId(transaction.getUserToId())
                        .amount(transaction.getAmount())
                        .date(transaction.getDate())
                        .comments(transaction.getComments())
                        .build()
        ).collect(Collectors.toList());
    }
}
