package com.bank.transactionservice.dto.response;

import com.bank.transactionservice.entity.TransactionStatus;
import com.bank.transactionservice.entity.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private String transactionReference;
    private TransactionType transactionType;
    private TransactionStatus status;
    private BigDecimal amount;
    private String fromAccountNumber;
    private String toAccountNumber;
    private String description;
    private LocalDateTime createdAt;
}
