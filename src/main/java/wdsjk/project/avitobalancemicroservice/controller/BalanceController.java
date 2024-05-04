package wdsjk.project.avitobalancemicroservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import wdsjk.project.avitobalancemicroservice.dto.request.ShowAndTransactionRequest;
import wdsjk.project.avitobalancemicroservice.dto.request.DepositRequest;
import wdsjk.project.avitobalancemicroservice.dto.request.TransferRequest;
import wdsjk.project.avitobalancemicroservice.dto.request.WithdrawRequest;

import wdsjk.project.avitobalancemicroservice.dto.response.BalanceResponse;
import wdsjk.project.avitobalancemicroservice.dto.response.TransactionResponse;

import wdsjk.project.avitobalancemicroservice.service.BalanceService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/balance")
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceService balanceService;

    @PostMapping("/deposit")
    public ResponseEntity<BalanceResponse> deposit(@RequestBody @Valid DepositRequest request) {
        return ResponseEntity.ok(balanceService.deposit(request));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<BalanceResponse> withdraw(@RequestBody @Valid WithdrawRequest request) {
        return ResponseEntity.ok(balanceService.withdraw(request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<BalanceResponse> transfer(@RequestBody @Valid TransferRequest request) {
        return ResponseEntity.ok(balanceService.transfer(request));
    }

    @GetMapping("/show")
    public ResponseEntity<BalanceResponse> show(@RequestBody @Valid ShowAndTransactionRequest request,
                                                @RequestParam(required = false) String currency) {
        return ResponseEntity.ok(balanceService.show(request, currency));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> transactions(@RequestBody @Valid ShowAndTransactionRequest request,
                                                                  @RequestParam(defaultValue = "10") Integer limit,
                                                                  @RequestParam(defaultValue = "amount") String orderBy) {
        return ResponseEntity.ok(balanceService.transactions(request, limit, orderBy));
    }
}
