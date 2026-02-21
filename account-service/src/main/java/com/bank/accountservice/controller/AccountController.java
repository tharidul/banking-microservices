package com.bank.accountservice.controller;

import com.bank.accountservice.dto.request.CreateAccountRequest;
import com.bank.accountservice.dto.response.AccountResponse;
import com.bank.accountservice.dto.response.ApiResponse;
import com.bank.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(@RequestBody CreateAccountRequest request){
        AccountResponse account = accountService.createAccount(request);
        return ResponseEntity.ok(
                ApiResponse.<AccountResponse>builder()
                        .success(true)
                        .data(account)
                        .message("Account created successfully")
                        .build()
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAccountsByUserId(@PathVariable Long userId){
        return ResponseEntity.ok(ApiResponse.<List<AccountResponse>>builder()
                .success(true)
                .data(accountService.getAccountsByUserId(userId))
                .message("Accounts fetched successfully")
                .build());
    }

    @GetMapping("/{id}")

    public ResponseEntity<ApiResponse<AccountResponse>> getAccountById(@PathVariable Long id){
        AccountResponse account = accountService.getAccountById(id);

        return ResponseEntity.ok(
                ApiResponse.<AccountResponse>builder()
                .success(true)
                .data(account)
                .message("Account fetched successfully")
                .build());
    }

    @GetMapping("/number/{accountNumber}")

    public ResponseEntity<ApiResponse<AccountResponse>> getAccountByNumber(@PathVariable String accountNumber){
        AccountResponse account = accountService.getAccountByAccountNumber(accountNumber);

        return ResponseEntity.ok(
                ApiResponse.<AccountResponse>builder()
                        .success(true)
                        .data(account)
                        .message("Account fetched successfully")
                        .build());
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<ApiResponse<AccountResponse>> closeAccount(@PathVariable Long id){

        AccountResponse account = accountService.closeAccount(id);
        return ResponseEntity.ok(
                ApiResponse.<AccountResponse>builder()
                        .success(true)
                        .data(account)
                        .message("Account closed successfully")
                        .build());
    }

    @PutMapping("/{accountNumber}/deposit")
    public ResponseEntity<ApiResponse<AccountResponse>> deposit(@PathVariable String accountNumber,@RequestParam BigDecimal amount){

        AccountResponse response = accountService.deposit(accountNumber,amount);
        return ResponseEntity.ok(
                ApiResponse.<AccountResponse>builder()
                        .success(true)
                        .data(response)
                        .message("Balance updated successfully")
                        .build()
        );
    }

    @PutMapping("/{accountNumber}/withdraw")
    public ResponseEntity<ApiResponse<AccountResponse>> withdraw(
            @PathVariable String accountNumber,@RequestParam BigDecimal amount){
        AccountResponse response = accountService.withdraw(accountNumber,amount);
        return ResponseEntity.ok(
                ApiResponse.<AccountResponse>builder()
                        .success(true)
                        .data(response)
                        .message("Balance updated successfully")
                        .build()
        );
    }



















}
