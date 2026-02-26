package com.bank.loanservice.service;

import com.bank.loanservice.client.AccountClient;
import com.bank.loanservice.dto.request.LoanRequest;
import com.bank.loanservice.dto.response.AccountResponse;
import com.bank.loanservice.dto.response.ApiResponse;
import com.bank.loanservice.dto.response.LoanResponse;
import com.bank.loanservice.entity.Loan;
import com.bank.loanservice.enums.LoanStatus;
import com.bank.loanservice.repository.LoanRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final AccountClient accountClient;

    @CircuitBreaker(name = "account-service", fallbackMethod = "applyForLoanFallback")
    public ApiResponse<LoanResponse> applyForLoan(LoanRequest request) {
        // Verify account exists via Feign
        ApiResponse<AccountResponse> accountResponse = accountClient.getAccount(request.getAccountNumber());

        if (!accountResponse.isSuccess() || accountResponse.getData() == null) {
            throw new RuntimeException("Account not found: " + request.getAccountNumber());
        }

        Loan loan = Loan.builder()
                .loanReference("LOAN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .accountNumber(request.getAccountNumber())
                .loanAmount(request.getLoanAmount())
                .remainingAmount(request.getLoanAmount())
                .termMonths(request.getTermMonths())
                .interestRate(request.getInterestRate())
                .status(LoanStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Loan saved = loanRepository.save(loan);
        log.info("Loan application created: {}", saved.getLoanReference());

        return ApiResponse.<LoanResponse>builder()
                .success(true)
                .message("Loan application submitted successfully")
                .data(mapToResponse(saved))
                .build();
    }

    public ApiResponse<List<LoanResponse>> getLoansByAccount(String accountNumber) {
        List<Loan> loans = loanRepository.findByAccountNumber(accountNumber);
        List<LoanResponse> responses = loans.stream().map(this::mapToResponse).toList();

        return ApiResponse.<List<LoanResponse>>builder()
                .success(true)
                .message("Loans retrieved successfully")
                .data(responses)
                .build();
    }

    private LoanResponse mapToResponse(Loan loan) {
        return LoanResponse.builder()
                .id(loan.getId())
                .loanReference(loan.getLoanReference())
                .accountNumber(loan.getAccountNumber())
                .loanAmount(loan.getLoanAmount())
                .remainingAmount(loan.getRemainingAmount())
                .termMonths(loan.getTermMonths())
                .interestRate(loan.getInterestRate())
                .status(loan.getStatus())
                .createdAt(loan.getCreatedAt())
                .build();
    }

    public ApiResponse<LoanResponse> applyForLoanFallback(LoanRequest request, Exception e) {
        log.error("Circuit breaker triggered for loan application: {}", e.getMessage());
        throw new RuntimeException("Account service is currently unavailable. Please try again later.");
    }
}
