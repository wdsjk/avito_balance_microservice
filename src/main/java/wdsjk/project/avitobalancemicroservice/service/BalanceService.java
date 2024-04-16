package wdsjk.project.avitobalancemicroservice.service;

import wdsjk.project.avitobalancemicroservice.dto.ShowRequest;

import wdsjk.project.avitobalancemicroservice.dto.request.DepositRequest;
import wdsjk.project.avitobalancemicroservice.dto.request.TransferRequest;
import wdsjk.project.avitobalancemicroservice.dto.request.WithdrawRequest;

import wdsjk.project.avitobalancemicroservice.dto.response.BalanceResponse;

public interface BalanceService {
    BalanceResponse deposit(DepositRequest request);

    BalanceResponse withdraw(WithdrawRequest request);

    BalanceResponse transfer(TransferRequest request);

    BalanceResponse show(ShowRequest request, String currency);
}
