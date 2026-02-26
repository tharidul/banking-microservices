package com.bank.loanservice.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private String accountHolderName;
    private BigDecimal balance;
    private String accountType;
    private String status;
}