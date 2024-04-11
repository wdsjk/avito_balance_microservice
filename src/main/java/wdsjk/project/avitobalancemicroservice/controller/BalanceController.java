package wdsjk.project.avitobalancemicroservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wdsjk.project.avitobalancemicroservice.dto.request.DepositRequest;
import wdsjk.project.avitobalancemicroservice.dto.response.BalanceResponse;
import wdsjk.project.avitobalancemicroservice.dto.request.WithdrawRequest;
import wdsjk.project.avitobalancemicroservice.service.BalanceService;

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
}
