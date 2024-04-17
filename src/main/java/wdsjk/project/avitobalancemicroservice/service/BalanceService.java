package wdsjk.project.avitobalancemicroservice.service;

import wdsjk.project.avitobalancemicroservice.dto.request.ShowAndTransactionRequest;

import wdsjk.project.avitobalancemicroservice.dto.request.DepositRequest;
import wdsjk.project.avitobalancemicroservice.dto.request.TransferRequest;
import wdsjk.project.avitobalancemicroservice.dto.request.WithdrawRequest;

import wdsjk.project.avitobalancemicroservice.dto.response.BalanceResponse;
import wdsjk.project.avitobalancemicroservice.dto.response.TransactionResponse;

import java.util.List;

public interface BalanceService {
    BalanceResponse deposit(DepositRequest request);

    BalanceResponse withdraw(WithdrawRequest request);

    BalanceResponse transfer(TransferRequest request);

    BalanceResponse show(ShowAndTransactionRequest request, String currency);

    List<TransactionResponse> transactions(ShowAndTransactionRequest request, Integer offset, Integer limit, String sortedBy);
}
