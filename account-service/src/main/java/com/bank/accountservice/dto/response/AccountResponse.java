package com.bank.accountservice.dto.response;

import com.bank.accountservice.entity.AccountStatus;
import com.bank.accountservice.entity.AccountType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {

    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private AccountStatus status;
    private BigDecimal balance;
    private Long userId;
    private LocalDateTime creaetdAt;
}
