package com.bank.accountservice.dto.request;

import com.bank.accountservice.entity.AccountType;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountRequest {

    private AccountType accountType;
    private Long userId;
}
