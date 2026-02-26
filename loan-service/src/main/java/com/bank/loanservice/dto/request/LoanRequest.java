package com.bank.loanservice.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequest {
    private String accountNumber;
    private BigDecimal loanAmount;
    private Integer termMonths;
    private Double interestRate;
}