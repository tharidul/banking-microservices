package com.bank.auditservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionReference;
    private String transactionType;
    private String fromAccountNumber;
    private BigDecimal amount;
    private String toAccountNumber;
    private String description;
    private LocalDateTime transactionCreatedAt;
    private LocalDateTime auditedAt;
}
