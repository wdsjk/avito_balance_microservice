package wdsjk.project.avitobalancemicroservice.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wdsjk.project.avitobalancemicroservice.dto.DepositRequest;
import wdsjk.project.avitobalancemicroservice.service.BalanceService;

@RestController
@RequestMapping("/api/v1/balance")
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceService balanceService;

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestBody DepositRequest request) {
        return ResponseEntity.ok(balanceService.deposit(request));
    }
}
