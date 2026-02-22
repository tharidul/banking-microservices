package com.bank.transactionservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "account-service", fallback = AccountClient.AccountClientFallback.class)
public interface AccountClient {

    @PutMapping("/api/v1/accounts/{accountNumber}/deposit")
    void deposit(@PathVariable("accountNumber") String accountNumber,
                 @RequestParam("amount") BigDecimal amount);

    @PutMapping("/api/v1/accounts/{accountNumber}/withdraw")
    void withdraw(@PathVariable("accountNumber") String accountNumber,
                  @RequestParam("amount") BigDecimal amount);

    @Component
    class AccountClientFallback implements AccountClient {

        @Override
        public void deposit(String accountNumber, BigDecimal amount) {
            throw new RuntimeException(
                    "Account service is unavailable. Please try again later. [DEPOSIT FALLBACK]"
            );
        }

        @Override
        public void withdraw(String accountNumber, BigDecimal amount) {
            throw new RuntimeException(
                    "Account service is unavailable. Please try again later. [WITHDRAW FALLBACK]"
            );
        }
    }
}