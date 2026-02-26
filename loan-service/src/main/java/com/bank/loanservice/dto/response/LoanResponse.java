package com.bank.loanservice.dto.response;

import com.bank.loanservice.enums.LoanStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanResponse {
    private Long id;
    private String loanReference;
    private String accountNumber;
    private BigDecimal loanAmount;
    private BigDecimal remainingAmount;
    private Integer termMonths;
    private Double interestRate;
    private LoanStatus status;
    private LocalDateTime createdAt;
}
