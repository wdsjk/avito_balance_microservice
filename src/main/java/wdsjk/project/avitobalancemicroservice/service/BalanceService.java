package wdsjk.project.avitobalancemicroservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import wdsjk.project.avitobalancemicroservice.domain.Balance;
import wdsjk.project.avitobalancemicroservice.dto.request.DepositRequest;
import wdsjk.project.avitobalancemicroservice.dto.response.BalanceResponse;
import wdsjk.project.avitobalancemicroservice.dto.request.WithdrawRequest;

import wdsjk.project.avitobalancemicroservice.exception.UserNotFoundException;
import wdsjk.project.avitobalancemicroservice.repository.BalanceRepository;

import java.util.UUID;

// TODO: Need to figure out what to do about bad floating point arithmetic!
@Service
@RequiredArgsConstructor
public class BalanceService {
    private final BalanceRepository balanceRepository;

    public BalanceResponse deposit(DepositRequest request) {
        Balance balance = balanceRepository.findByUserId(request.userId()).orElse(null);

        if (null != balance) {
            balance.setAmountOfMoney(balance.getAmountOfMoney() + request.amountOfMoney());
        } else {
            balance = Balance.builder()
                        .id(UUID.randomUUID().toString())
                        .userId(request.userId())
                        .amountOfMoney(request.amountOfMoney())
                    .build();
        }
        balanceRepository.save(balance);

        return new BalanceResponse("Money has been successfully deposited");
    }

    @Transactional
    public BalanceResponse withdraw(WithdrawRequest request) {
        Balance balance = balanceRepository.findByUserId(request.userId()).orElseThrow(
                () -> new UserNotFoundException(
                        String.format("User with id: %s is not found!", request.userId())
                )
        );

        balance.setAmountOfMoney(balance.getAmountOfMoney() - request.amountOfMoney());

        return new BalanceResponse("Money has been successfully withdrew");
    }
}
