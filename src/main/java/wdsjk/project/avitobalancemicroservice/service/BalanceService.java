package wdsjk.project.avitobalancemicroservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import wdsjk.project.avitobalancemicroservice.domain.Balance;
import wdsjk.project.avitobalancemicroservice.dto.ShowRequest;
import wdsjk.project.avitobalancemicroservice.dto.request.DepositRequest;
import wdsjk.project.avitobalancemicroservice.dto.request.TransferRequest;
import wdsjk.project.avitobalancemicroservice.dto.response.BalanceResponse;
import wdsjk.project.avitobalancemicroservice.dto.request.WithdrawRequest;

import wdsjk.project.avitobalancemicroservice.exception.UserNotFoundException;
import wdsjk.project.avitobalancemicroservice.repository.BalanceRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.UUID;

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

    public BalanceResponse show(ShowRequest request, String currency) throws IOException {
        BigDecimal amount = balanceRepository.findByUserId(request.userId()).orElseThrow(
                () -> new UserNotFoundException(String.format("User with id: %s is not found!", request.userId()))
        ).getAmountOfMoney();

        if (null != currency) {
            String url = "https://api.freecurrencyapi.com/v1/latest?apikey=fca_live_B2FeYk8cwUtZhT2Q0CNSZSfuswYpsLMk3yfMwTQb";
            // TODO: Finish
        }

        return new BalanceResponse(String.valueOf(amount));
    }
}
