package com.bank.transactionservice.kafka.event;

import com.bank.transactionservice.entity.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {
    private String transactionReference;
    private TransactionType transactionType;
    private BigDecimal amount;
    private String fromAccountNumber;
    private String toAccountNumber;
    private String description;
    private LocalDateTime createdAt;
}
