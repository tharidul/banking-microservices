package com.bank.transactionservice.controller;

import com.bank.transactionservice.dto.request.TransactionRequest;
import com.bank.transactionservice.dto.response.ApiResponse;
import com.bank.transactionservice.dto.response.TransactionResponse;
import com.bank.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.shaded.com.google.protobuf.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(@RequestBody TransactionRequest request){
        TransactionResponse transactionResponse = transactionService.deposit(request);

        return  ResponseEntity.ok(
                ApiResponse.<TransactionResponse>builder()
                        .success(true)
                        .message("Deposit successful")
                        .data(transactionResponse)
                        .build()
        );
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(@RequestBody TransactionRequest request){
        TransactionResponse transactionResponse = transactionService.withdraw(request);

        return  ResponseEntity.ok(
                ApiResponse.<TransactionResponse>builder()
                        .success(true)
                        .message("Withdrawal successful")
                        .data(transactionResponse)
                        .build()
        );
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(@RequestBody TransactionRequest request) {
        TransactionResponse transactionResponse = transactionService.transfer(request);
        return ResponseEntity.ok(
                ApiResponse.<TransactionResponse>builder()
                        .success(true)
                        .message("Transfer successful")
                        .data(transactionResponse)
                        .build()
        );
    }

    @GetMapping("account/{accountNumber}")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByAccount(@PathVariable String accountNumber){
        List<TransactionResponse> transactions = transactionService.getTransactionsByAccount(accountNumber);
        return ResponseEntity.ok(
                ApiResponse.<List<TransactionResponse>>builder()
                        .success(true)
                        .message("Transactions fetched successfully")
                        .data(transactions)
                        .build()
        );
    }

    @GetMapping("/{reference}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionByReference(@PathVariable String reference){
        TransactionResponse transaction = transactionService.getTransactionByReference(reference);
        return ResponseEntity.ok(
                ApiResponse.<TransactionResponse>builder()
                        .success(true)
                        .message("Transaction fetched successfully")
                        .data(transaction)
                        .build()
        );
    }
}
