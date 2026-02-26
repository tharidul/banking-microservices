package com.bank.loanservice.controller;

import com.bank.loanservice.dto.request.LoanRequest;
import com.bank.loanservice.dto.response.ApiResponse;
import com.bank.loanservice.dto.response.LoanResponse;
import com.bank.loanservice.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<LoanResponse>> applyForLoan(@RequestBody LoanRequest request) {
        return ResponseEntity.ok(loanService.applyForLoan(request));
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getLoansByAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(loanService.getLoansByAccount(accountNumber));
    }
}
