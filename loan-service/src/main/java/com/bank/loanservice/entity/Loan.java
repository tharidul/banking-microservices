package com.bank.loanservice.entity;

import com.bank.loanservice.enums.LoanStatus;
import jakarta.persistence.*;
import jakarta.persistence.Table;import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loanReference;
    private String accountNumber;
    private BigDecimal loanAmount;
    private BigDecimal remainingAmount;
    private Integer termMonths;
    private Double interestRate;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}