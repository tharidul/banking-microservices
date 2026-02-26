package com.bank.notificationservice.kafka.event;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEvent {
    private String transactionReference;
    private String transactionType;
    private String amount;
    private String fromAccountNumber;
    private String toAccountNumber;
    private String description;
    private LocalDateTime createAt;
}
