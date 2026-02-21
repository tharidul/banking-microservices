package com.bank.transactionservice.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
    private String description;
}
